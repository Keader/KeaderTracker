package dev.keader.correiosapi.util

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun Call.executeSuspend() = suspendCancellableCoroutine<ResponseBody> { continuation ->
    this.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            continuation.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            continuation.resume(response.peekBody(Long.MAX_VALUE))
        }
    })

    continuation.invokeOnCancellation { this.cancel() }
}
