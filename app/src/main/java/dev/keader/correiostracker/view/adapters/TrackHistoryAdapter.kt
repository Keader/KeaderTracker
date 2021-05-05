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
const val BUTTON_BACK = 1
const val BUTTON_COPY = 2
const val BUTTON_DELETE = 3
const val BUTTON_SHARE = 4

class TrackHistoryAdapter(private val deleteClickListener: TrackHistoryButtonListener)
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
                holder.bind(item.track, position, currentList.size)
            }
            is TrackDetailHeaderViewHolder -> {
                val item = getItem(position) as DataItem.Header
                holder.bind(item.itemWithTracks, deleteClickListener)
            }
        }
    }

    fun addHeaderAndSubmitList(item: ItemWithTracks) {
        adapterScope.launch {
            val items = listOf(DataItem.Header(item)) +
                item.tracks.map {
                    DataItem.TrackWithStatusItem(
                        TrackWithStatus(it, item.item.isDelivered, item.item.isWaitingPost)
                    )
                }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    class TrackDetailHeaderViewHolder private constructor(val binding: ListItemTrackDetailHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemWithTracks, clickListener: TrackHistoryButtonListener) {
            binding.itemWithTracks = item
            binding.clickListener = clickListener
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

        fun bind(track: TrackWithStatus, position: Int, size: Int) {
            binding.track = track.track
            val isDelivery = track.track.status.contains(
                binding.root.context.getString(R.string.delivery_message))
            handleIconChange(track, position, size, isDelivery)
            binding.executePendingBindings()
        }

        private fun handleIconChange(track: TrackWithStatus, position: Int,
                                     size: Int, isDelivery: Boolean) {
            // We only have Header and 1 element
            if (size == 2) {
                binding.arrowUp.visibility = View.GONE
                binding.arrowDown.visibility = View.GONE
                if (track.isWaitingPost)
                    binding.trackIcon.setImageResource(R.drawable.waiting)
                else
                    binding.trackIcon.setImageResource(R.drawable.posted)
            } else {
                // First element
                if (position == 1) {
                    binding.arrowUp.visibility = View.GONE
                    binding.arrowDown.visibility = View.VISIBLE

                    if (track.delivered)
                        binding.trackIcon.setImageResource(R.drawable.delivery_done)
                    else
                        binding.trackIcon.setImageResource(R.drawable.delivery_truck)
                }

                var lastPosition: Int
                if (track.isWaitingPost) {
                    // if we have waiting post, last element will be waiting post dummy
                    // so we need get last but one item to add posted icon
                    lastPosition = size - 1
                    val lastButOne = size - 2

                    if (position == lastPosition) {
                        binding.arrowDown.visibility = View.GONE
                        binding.arrowUp.visibility = View.VISIBLE
                        binding.trackIcon.setImageResource(R.drawable.waiting)
                    } else if (position == lastButOne)
                        binding.trackIcon.setImageResource(R.drawable.posted)

                } else {
                    lastPosition = size - 1
                    if (position == lastPosition) {
                        binding.arrowDown.visibility = View.GONE
                        binding.arrowUp.visibility = View.VISIBLE
                        binding.trackIcon.setImageResource(R.drawable.posted)
                    }
                }

                // It should be handled independent of position
                if (isDelivery)
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
    data class TrackWithStatusItem(val track: TrackWithStatus) : DataItem() {
        override val id = track.track.trackUid
    }

    data class Header(val itemWithTracks: ItemWithTracks) : DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}

class TrackHistoryButtonListener(val clickListener: (itemTrack: ItemWithTracks, id: Int) -> Unit) {
    fun onClick(item: ItemWithTracks, id: Int) = clickListener(item, id)
}

class BackButtonListener(val clickListener: () -> Unit) {
    fun onClick() = clickListener()
}
