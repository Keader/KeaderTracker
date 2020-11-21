package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.TrackWithStatus
import dev.keader.correiostracker.databinding.ListItemTrackDetailHeaderBinding
import dev.keader.correiostracker.databinding.ListItemTrackHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class TrackHistoryAdapter(private val deleteClickListener: DeleteItemListener,
                          private val backClickListener: BackButtonListener)
    : ListAdapter<DataItem, RecyclerView.ViewHolder>(TrackDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TrackDetailHeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> TrackDetailItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.TrackWithStatusItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrackDetailItemViewHolder -> {
                val item = getItem(position) as DataItem.TrackWithStatusItem
                holder.bind(item.track, item == currentList[1], item == currentList.last())
            }
            is TrackDetailHeaderViewHolder -> {
                val item = getItem(position) as DataItem.Header
                holder.bind(item.itemWithTracks, deleteClickListener, backClickListener)
            }
        }
    }

    fun addHeaderAndSubmitList(item: ItemWithTracks) {
        adapterScope.launch {
            val items = listOf(DataItem.Header(item)) +
                    item.tracks.map {
                        DataItem.TrackWithStatusItem(
                                TrackWithStatus(it, item.item.isDelivered)
                        )}
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class TrackDetailHeaderViewHolder private constructor(val binding: ListItemTrackDetailHeaderBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemWithTracks, clickListener: DeleteItemListener, backClickListener: BackButtonListener) {
            binding.itemWithTracks = item
            binding.clickListener = clickListener
            binding.backClickListener = backClickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TrackDetailHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrackDetailHeaderBinding.inflate(layoutInflater, parent, false)
                return TrackDetailHeaderViewHolder(binding)
            }
        }
    }

    class TrackDetailItemViewHolder private constructor(val binding: ListItemTrackHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: TrackWithStatus, isFirstElement: Boolean, isLastElement: Boolean) {
            binding.track = track.track
            val isDelivery = track.track.status.contains(
                    binding.root.context.getString(R.string.delivery_message))
            if (isFirstElement || isLastElement || isDelivery)
                handleIconChange(track, isFirstElement, isLastElement, isDelivery)
            binding.executePendingBindings()
        }

        private fun handleIconChange(track: TrackWithStatus, firstElement: Boolean,
                                      lastElement: Boolean, isDelivery: Boolean) {
            if (firstElement) {
                binding.arrowUp.visibility = View.GONE
                binding.arrowDown.visibility = View.VISIBLE
                if (track.delivered)
                    binding.trackIcon.setImageResource(R.drawable.delivery_done)
                else
                    binding.trackIcon.setImageResource(R.drawable.delivery_truck)
            } else if (lastElement) {
                binding.arrowDown.visibility = View.GONE
                binding.arrowUp.visibility = View.VISIBLE
                binding.trackIcon.setImageResource(R.drawable.posted)
            } else if (isDelivery) {
                binding.trackIcon.setImageResource(R.drawable.delivery_progress)
            }
        }

        companion object {
            fun from(parent: ViewGroup): TrackDetailItemViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrackHistoryBinding.inflate(layoutInflater, parent, false)
                return TrackDetailItemViewHolder(binding)
            }
        }
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<DataItem>() {

    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class DataItem {
    data class TrackWithStatusItem(val track: TrackWithStatus): DataItem() {
        override val id = track.track.trackUid
    }

    data class Header(val itemWithTracks: ItemWithTracks): DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}

class DeleteItemListener(val clickListener: (itemTrack: ItemWithTracks) -> Unit) {
    fun onClick(item: ItemWithTracks) = clickListener(item)
}

class BackButtonListener(val clickListener: () -> Unit) {
    fun onClick() = clickListener()
}
