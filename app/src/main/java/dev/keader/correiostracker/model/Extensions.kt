package dev.keader.correiostracker.model

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import java.io.File
import java.io.FileOutputStream
import java.util.*

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

fun String.toCapitalize(): String {
    return replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase(Locale.getDefault())
        else
            it.toString()
    }
}

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> {
    return Transformations.distinctUntilChanged(this)
}

fun Handler.removeOldEvents() {
    removeCallbacksAndMessages(null)
}

fun Activity.windowInsetsControllerCompat(view: View): WindowInsetsControllerCompat? {
    return WindowCompat.getInsetsController(window, view)
}

val View.windowInsetsControllerCompat: WindowInsetsControllerCompat?
    get() = ViewCompat.getWindowInsetsController(this)

fun View.closeKeyboard() {
    windowInsetsControllerCompat?.hide(WindowInsetsCompat.Type.ime())
}

fun View.openKeyboard() {
    windowInsetsControllerCompat?.show(WindowInsetsCompat.Type.ime())
}
