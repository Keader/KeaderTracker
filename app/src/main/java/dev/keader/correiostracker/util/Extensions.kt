package dev.keader.correiostracker.util

import android.content.Context
import android.graphics.Bitmap
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
