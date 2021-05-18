package dev.keader.correiostracker.view.capture

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import timber.log.Timber

class TrackingCodeDetectionProcessor (
    private val actions: CodeDetectionActions
) : ImageAnalysis.Analyzer {
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var recognizer: TextRecognizer
    private var analyzing = false

    fun start() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        barcodeScanner = BarcodeScanning.getClient(options)
        recognizer = TextRecognition.getClient()
        analyzing = true
    }

    @ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        if (!analyzing) {
            image.close()
            return
        }

        val mediaImage = image.image
        mediaImage ?: return

        val input = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

        val (detected, source) = detectFromBarcode(input) ?: detectFromOCR(input) ?: null to -1

        if (detected != null) {
            actions.onCodeDetected(detected, source)
            stop()
        }

        mediaImage.close()
        image.close()
    }

    private fun detectFromBarcode(input: InputImage): Pair<String, Int>? {
        val codes = try {
            Tasks.await(barcodeScanner.process(input))
        } catch (error: Throwable) {
            Timber.d(error, "Error during barcode scan")
            emptyList()
        }

        val result = codes
            .mapNotNull { it.displayValue }
            .also { Timber.d("$it") }
            .firstOrNull { it.matches(TRACK_CODE_PATTERN) }
        return if (result == null) {
            null
        } else {
            result to CodeDetectionActions.SOURCE_QR_CODE
        }
    }

    private fun detectFromOCR(input: InputImage): Pair<String, Int>? {
        val result = try {
            Tasks.await(recognizer.process(input))
        } catch (error: Throwable) {
            Timber.d("Error during OCR scan")
            null
        }

        result ?: return null

        val lineTexts = result.textBlocks.flatMap { it.lines }

        val lines = lineTexts.mapNotNull { line ->
            val height = line.boundingBox?.height() ?: 0
            if (height < 10) {
                null
            } else {
                val match = TRACK_CODE_PATTERN.find(line.text)
                match?.value
            }
        }

        // give precedence to the first text that matches criteria
        val detected = lines.firstOrNull()
        return if (detected == null) {
            null
        } else {
            detected to CodeDetectionActions.SOURCE_OCR
        }
    }

    fun stop() {
        Timber.d("Stop called")
        analyzing = false
        barcodeScanner.close()
        recognizer.close()
    }

    companion object {
        val TRACK_CODE_PATTERN = Regex("[A-Za-z]{2}[0-9]{9}[A-Za-z]{2}")
    }
}