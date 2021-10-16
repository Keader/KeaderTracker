package dev.keader.correiostracker.model.tree

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.DebugTree() {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority != Log.ERROR && priority != Log.ASSERT) {
            return
        }

        crashlytics.setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
        tag?.also { crashlytics.setCustomKey(CRASHLYTICS_KEY_TAG, it) }
        crashlytics.setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)

        t?.let{
            crashlytics.recordException(it)
        } ?: handleWithMessage(message)
    }

    private fun handleWithMessage(message: String) {
        val exception = Exception(message)
        exception.stackTrace = exception.stackTrace.filter {
            it.className !in ignoreList
        }.toTypedArray()
        crashlytics.recordException(exception)
        crashlytics.sendUnsentReports()
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        val stackElement = Exception().stackTrace.first {  it.className !in ignoreList }
        return "${super.createStackElementTag(stackElement)}:${stackElement.methodName}(${stackElement.lineNumber})"
    }

    companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
        val ignoreList = listOf(
            Timber::class.java.name,
            Timber.Forest::class.java.name,
            Timber.Tree::class.java.name,
            Timber.DebugTree::class.java.name,
            CrashlyticsTree::class.java.name
        )
    }
}
