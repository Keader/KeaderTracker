package dev.keader.correiostracker.view.capture

import androidx.annotation.IntDef

interface CodeDetectionActions {
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [SOURCE_OCR, SOURCE_QR_CODE])
    annotation class DetectionSource

    fun onCodeDetected(code: String, @DetectionSource source: Int)

    companion object {
        const val SOURCE_OCR = 1
        const val SOURCE_QR_CODE = 2
    }
}