package dev.keader.correiostracker.model

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.core.view.*
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

fun <T> MutableLiveData<T>.setValueIfNew(newValue: T) {
    if (this.value != newValue) value = newValue
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

fun View.inflater(): LayoutInflater = LayoutInflater.from(context)

inline fun <reified T : ViewDataBinding> ViewGroup.inflate(@LayoutRes res: Int, attachToParent: Boolean = false): T {
    val inflater = inflater()
    return DataBindingUtil.inflate(inflater, res, this, attachToParent)
}

inline fun <reified T : ViewDataBinding> LayoutInflater.inflate(@LayoutRes res: Int): T {
    return DataBindingUtil.inflate(this, res, null, false)
}

inline fun <T : ViewDataBinding> T.executeBindingsAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}

fun RecyclerView.clearDecorations() {
    if (itemDecorationCount > 0) {
        for (i in itemDecorationCount - 1 downTo 0) {
            removeItemDecorationAt(i)
        }
    }
}

fun View.fadeIn() {
    val fadeInAnim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
    visibility = View.VISIBLE
    startAnimation(fadeInAnim)
    requestLayout()
}

fun View.doOnApplyWindowInsets(f: (View, WindowInsetsCompat, InitialPadding) -> Unit) {
    val initialPadding = recordInitialPaddingForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, initialPadding)
        insets
    }
    requestApplyInsetsWhenAttached()
}

fun View.doOnApplyWindowMarginInsets(f: (View, WindowInsetsCompat, InitialPadding) -> Unit) {
    val initialMargin = recordInitialMarginForView(this)
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        f(v, insets, initialMargin)
        insets
    }
    requestApplyInsetsWhenAttached()
}

private fun recordInitialPaddingForView(view: View) = InitialPadding(
    view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom
)

private fun recordInitialMarginForView(view: View) = InitialPadding(
    view.marginLeft, view.marginTop, view.marginRight, view.marginBottom
)

fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        ViewCompat.requestApplyInsets(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                ViewCompat.requestApplyInsets(v)
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}

data class InitialPadding(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

@BindingAdapter(
    "paddingStartSystemWindowInsets",
    "paddingTopSystemWindowInsets",
    "paddingEndSystemWindowInsets",
    "paddingBottomSystemWindowInsets",
    "consumeSystemWindowInsets",
    requireAll = false
)
fun applySystemWindows(
    view: View,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean,
    consumeInsets: Boolean = false
) {
    view.doOnApplyWindowInsets { _, allInsets, padding ->
        val insets = allInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        val left = if (applyLeft) insets.left else 0
        val top = if (applyTop) insets.top else 0
        val right = if (applyRight) insets.right else 0
        val bottom = if (applyBottom) insets.bottom else 0

        view.setPadding(
            padding.left + left,
            padding.top + top,
            padding.right + right,
            padding.bottom + bottom
        )

        if (consumeInsets) {
            WindowInsetsCompat.CONSUMED
        }
    }
}

@BindingAdapter(
    "marginStartSystemWindowInsets",
    "marginTopSystemWindowInsets",
    "marginEndSystemWindowInsets",
    "marginBottomSystemWindowInsets",
    requireAll = false
)
fun applyMarginSystemWindows(
    view: View,
    applyLeft: Boolean,
    applyTop: Boolean,
    applyRight: Boolean,
    applyBottom: Boolean
) {
    view.doOnApplyWindowMarginInsets { _, allInsets, margins ->
        val insets = allInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        val left = if (applyLeft) insets.left else 0
        val top = if (applyTop) insets.top else 0
        val right = if (applyRight) insets.right else 0
        val bottom = if (applyBottom) insets.bottom else 0

        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            setMargins(
                margins.left + left,
                margins.top + top,
                margins.right + right,
                margins.bottom + bottom
            )
        }?.also {
            view.layoutParams = it
        }
    }
}

@BindingAdapter("goneIf")
fun goneIf(view: View, gone: Boolean) {
    view.visibility = if (gone) View.GONE else View.VISIBLE
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, condition: Boolean) {
    view.visibility = if (condition) View.VISIBLE else View.GONE
}

@BindingAdapter("invisibleUnless")
fun invisibleUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("swipeRefreshColors")
fun setSwipeRefreshColors(swipeRefreshLayout: SwipeRefreshLayout, colorResIds: IntArray) {
    swipeRefreshLayout.setColorSchemeColors(*colorResIds)
}

@BindingAdapter("refreshing")
fun swipeRefreshing(refreshLayout: SwipeRefreshLayout, refreshing: Boolean) {
    refreshLayout.isRefreshing = refreshing
}

@BindingAdapter("onSwipeRefresh")
fun onSwipeRefresh(view: SwipeRefreshLayout, function: SwipeRefreshLayout.OnRefreshListener) {
    view.setOnRefreshListener(function)
}

@BindingAdapter("swipeEnabled")
fun swipeEnabled(view: SwipeRefreshLayout, enabled: Boolean) {
    view.isEnabled = enabled
}
