package dev.keader.correiostracker.view.adapters

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.R
import dev.keader.correiostracker.databinding.ListItemTrackHeaderBinding


class TrackHeaderAdapter() : RecyclerView.Adapter<TrackHeaderAdapter.TrackHeaderViewHolder>() {

    companion object {
        private val animList = listOf(
            R.raw.area_map,
            R.raw.delivery_box,
            R.raw.service_area
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHeaderViewHolder {
        return TrackHeaderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TrackHeaderViewHolder, position: Int) { }

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

                binding.animDelivery.setAnimation(animList.random())
                return TrackHeaderViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = 1
}
