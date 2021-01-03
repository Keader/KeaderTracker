package dev.keader.correiostracker.view.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.databinding.ListItemTrackBinding
import dev.keader.correiostracker.databinding.ListItemTrackHeaderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1
private const val HEADER_ID = "HEADER"

class TrackAdapter(private val itemClickListener: ListItemListener,
                   private val isHomeScreen: Boolean) : ListAdapter<TrackData, RecyclerView.ViewHolder>(ItemWitchTracksDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> TrackHeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> TrackViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TrackData.Header -> ITEM_VIEW_TYPE_HEADER
            is TrackData.ItemWithTracksItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun addHeaderAndSubmitList(items: List<ItemWithTracks>) {
        adapterScope.launch {
            val list = mutableListOf<TrackData>()
            if (isHomeScreen)
                list.add(TrackData.Header(HEADER_ID))
            list.addAll(items.map { TrackData.ItemWithTracksItem(it) })
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TrackViewHolder -> {
                val item = getItem(position) as TrackData.ItemWithTracksItem
                holder.bind(itemClickListener, item.item)
            }
            //is TrackHeaderViewHolder -> { }
        }
    }

    class TrackHeaderViewHolder private constructor(val binding: ListItemTrackHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): TrackHeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrackHeaderBinding.inflate(layoutInflater, parent, false)
                val displayMetrics = Resources.getSystem().displayMetrics
                // Remove animation, in 720p devices :/
                if (displayMetrics.widthPixels < 800 && displayMetrics.heightPixels < 1300)
                    binding.animDelivery.visibility = View.GONE
                return TrackHeaderViewHolder(binding)
            }
        }
    }

    class TrackViewHolder private constructor(val binding: ListItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: ListItemListener, item: ItemWithTracks) {
            binding.trackItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): RecyclerView.ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrackBinding.inflate(layoutInflater, parent, false)
                return TrackViewHolder(binding)
            }
        }
    }
}

class ItemWitchTracksDiffCallback : DiffUtil.ItemCallback<TrackData>() {

    override fun areItemsTheSame(oldItem: TrackData, newItem: TrackData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TrackData, newItem: TrackData): Boolean {
        return oldItem == newItem
    }
}

class ListItemListener(val clickListener: (trackCode: String) -> Unit) {
    fun onClick(item: ItemWithTracks) = clickListener(item.item.code)
}

sealed class TrackData {
    data class ItemWithTracksItem(val item: ItemWithTracks) : TrackData() {
        override val id = item.item.code
    }

    data class Header(val myId: String) : TrackData() {
        override val id = myId
    }

    abstract val id: String
}
