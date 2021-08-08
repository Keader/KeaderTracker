package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.databinding.ListItemTrackBinding

class TrackAdapter(private val itemClickListener: ListItemListener) :
    ListAdapter<ItemWithTracks, TrackAdapter.TrackViewHolder>(TrackAdapter) {

    private companion object : DiffUtil.ItemCallback<ItemWithTracks>() {

        override fun areItemsTheSame(oldItem: ItemWithTracks, newItem: ItemWithTracks): Boolean {
            return oldItem.item.code == newItem.item.code
        }

        override fun areContentsTheSame(oldItem: ItemWithTracks, newItem: ItemWithTracks): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(itemClickListener, item)
    }

    class TrackViewHolder private constructor(val binding: ListItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: ListItemListener, item: ItemWithTracks) {
            binding.trackItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): TrackViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrackBinding.inflate(layoutInflater, parent, false)
                return TrackViewHolder(binding)
            }
        }
    }
}

class ListItemListener(val clickListener: (trackCode: String) -> Unit) {
    fun onClick(item: ItemWithTracks) = clickListener(item.item.code)
}
