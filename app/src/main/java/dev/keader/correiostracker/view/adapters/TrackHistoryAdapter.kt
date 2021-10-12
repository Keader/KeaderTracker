package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.ListItemTrackDetailHeaderBinding
import dev.keader.correiostracker.databinding.ListItemTrackHistoryBinding
import dev.keader.sharedapiobjects.DeliveryCompany
import dev.keader.sharedapiobjects.ItemWithTracks
import dev.keader.sharedapiobjects.TrackWithStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class TrackHistoryAdapter(private val deleteClickListener: TrackHistoryButtonListener)
    : ListAdapter<DataItem, RecyclerView.ViewHolder>(TrackDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private lateinit var itemWithTracks: ItemWithTracks

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
                holder.bind(item.track, itemWithTracks, position, currentList.size)
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
                itemWithTracks = item
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

        fun bind(track: TrackWithStatus, item: ItemWithTracks, position: Int, size: Int) {
            binding.track = track.track
            binding.executePendingBindings()
            handleWithStatus(track, item)
            handleWithArrows(position, size)
        }

        private fun handleWithArrows(position: Int, size: Int) {
            when {
                // Header + 1 element
                size == 2 -> {
                    binding.arrowUp.visibility = View.GONE
                    binding.arrowDown.visibility = View.GONE
                }
                position == 1 -> {
                    binding.arrowUp.visibility = View.GONE
                    binding.arrowDown.visibility = View.VISIBLE
                }
                position == (size - 1) -> {
                    binding.arrowDown.visibility = View.GONE
                    binding.arrowUp.visibility = View.VISIBLE
                }
            }
        }

        private fun handleWithStatus(track: TrackWithStatus, item: ItemWithTracks) {
            when (val company = item.item.deliveryCompany) {
                DeliveryCompany.CORREIOS -> handleCorreiosStatus(track)
                else -> Timber.e("TrackDetailItemViewHolder get a unknown delivery company: $company")
            }
        }

        private fun handleCorreiosStatus(itemTrack: TrackWithStatus) {
            val context = binding.root.context
            val status = CorreiosResourceStatus.getResourceStatus(itemTrack.track.status)
            val titleColor = ContextCompat.getColor(context, status.titleColorRes)
            binding.trackIcon.setImageResource(status.iconRes)
            binding.statusLabel2.setTextColor(titleColor)
            val linkColor = ContextCompat.getColor(context, R.color.primaryLightColor)
            binding.importsLink.setLinkTextColor(linkColor)
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

class TrackHistoryButtonListener(val clickListener: (itemTrack: ItemWithTracks, id: TrackHistoryButtonTypes) -> Unit) {
    fun onClick(item: ItemWithTracks, id: TrackHistoryButtonTypes) = clickListener(item, id)
}

enum class TrackHistoryButtonTypes {
    BUTTON_BACK,
    BUTTON_COPY,
    BUTTON_DELETE,
    BUTTON_SHARE,
    BUTTON_EDIT
}
