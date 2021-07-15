package dev.keader.correiostracker.view.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.databinding.ListItemTrackHeaderBinding

/**
 * @author Samuel da Costa Araujo Nunes
 * Created 15/07/2021 at 20:01
 */
class HeaderAdapter : ListAdapter<String, HeaderAdapter.TrackHeaderViewHolder>(HeaderAdapter) {

    private companion object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = true
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHeaderViewHolder {
        return TrackHeaderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TrackHeaderViewHolder, position: Int) {}

    class TrackHeaderViewHolder private constructor(val binding: ListItemTrackHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
}
