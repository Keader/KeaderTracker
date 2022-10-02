package dev.keader.correiostracker.model.tree

import timber.log.Timber

class CorreiosTrackerDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${super.createStackElementTag(element)}:${element.methodName}(${element.lineNumber})"
    }
}
