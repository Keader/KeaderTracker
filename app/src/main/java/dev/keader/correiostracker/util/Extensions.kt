package dev.keader.correiostracker.util

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import java.io.File
import java.io.FileOutputStream

fun Bitmap.toFile(context: Context): File {
    val parent = File(context.getExternalFilesDir(null), "tracks")
    parent.deleteRecursively()
    parent.mkdirs()
    val file = File(parent, "track_share.jpg")
    val fos = FileOutputStream(file)
    compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.flush()
    fos.close()
    return file
}

fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}
