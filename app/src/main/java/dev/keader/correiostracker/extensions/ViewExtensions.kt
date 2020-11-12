package dev.keader.correiostracker.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.validate(message: String, validator: (String) -> Boolean) {
    this.doAfterTextChanged {
        this.error = if (validator(it.toString())) null else message
    }
}