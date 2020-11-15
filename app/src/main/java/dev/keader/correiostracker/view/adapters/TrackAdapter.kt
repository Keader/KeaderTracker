package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.databinding.ListItemTrackBinding


class TrackAdapter(val clickListener: TrackItemListener) : ListAdapter<ItemWithTracks, TrackAdapter.ViewHolder>(ItemWitchTracksDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)
    }

    class ViewHolder private constructor(val binding: ListItemTrackBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: TrackItemListener, item: ItemWithTracks) {
            binding.trackItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrackBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ItemWitchTracksDiffCallback : DiffUtil.ItemCallback<ItemWithTracks>() {

    override fun areItemsTheSame(oldItem: ItemWithTracks, newItem: ItemWithTracks): Boolean {
        return oldItem.item.code == newItem.item.code
    }

    override fun areContentsTheSame(oldItem: ItemWithTracks, newItem: ItemWithTracks): Boolean {
        return oldItem == newItem
    }
}

class TrackItemListener(val clickListener: (trackCode: String) -> Unit) {
    fun onClick(item: ItemWithTracks) = clickListener(item.item.code)
}
