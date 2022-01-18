package dev.keader.correiostracker.view.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.R

class SwipeDeleteHandler(
    context: Context,
    private val onDelete: (RecyclerView.ViewHolder) -> Unit,
    direction: Int = ItemTouchHelper.LEFT,
    private val ignored: List<Class<*>> = emptyList()
) : ItemTouchHelper.SimpleCallback(0, direction) {
    private val background = ColorDrawable(Color.LTGRAY)
    private val xMark = ContextCompat.getDrawable(context, R.drawable.ic_github)
    private val xMarkMargin = context.resources.getDimension(R.dimen.delete_margin).toInt()

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onDelete(viewHolder)
    }

    override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return when (viewHolder::class.java) {
            in ignored -> 0
            else -> super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (viewHolder.bindingAdapterPosition < 0) return
        val view = viewHolder.itemView // the view being swiped

        val context = view.context
        val inflated = LayoutInflater.from(context).inflate(R.layout.item_track_swipe_bg, null)
        inflated.invalidate()
        inflated.measure(view.width, view.height)
        inflated.layout(view.right + dX.toInt(), view.top, view.right, view.bottom)
        c.save()
        c.translate(inflated.right + dX, (viewHolder.absoluteAdapterPosition * view.height).toFloat())
        inflated.draw(c)
        c.restore()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
