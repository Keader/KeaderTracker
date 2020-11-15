package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.TrackWithStatus
import dev.keader.correiostracker.databinding.ListItemTrackHistoryBinding

class TrackHistoryAdapter() : ListAdapter<TrackWithStatus, TrackHistoryAdapter.ViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,item == currentList.first(), item == currentList.last())
    }

    class ViewHolder private constructor(val binding: ListItemTrackHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: TrackWithStatus, isFirstElement: Boolean, isLastElement: Boolean) {
            binding.track = track.track
            if (isFirstElement || isLastElement)
                handleIconChange(track, isFirstElement, isLastElement)
            binding.executePendingBindings()
        }

        private fun handleIconChange(track: TrackWithStatus,
                                     firstElement: Boolean, lastElement: Boolean) {
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
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrackHistoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<TrackWithStatus>() {

    override fun areItemsTheSame(oldItem: TrackWithStatus, newItem: TrackWithStatus): Boolean {
        return oldItem.track.trackUid == newItem.track.trackUid
    }

    override fun areContentsTheSame(oldItem: TrackWithStatus, newItem: TrackWithStatus): Boolean {
        return oldItem == newItem
    }
}
