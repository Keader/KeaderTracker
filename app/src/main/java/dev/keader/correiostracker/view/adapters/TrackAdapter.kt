package dev.keader.correiostracker.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.keader.correiostracker.databinding.ListItemTrackBinding
import dev.keader.correiostracker.view.interfaces.TrackItemListener
import dev.keader.sharedapiobjects.DeliveryCompany
import dev.keader.sharedapiobjects.ItemWithTracks
import timber.log.Timber

class TrackAdapter(private val trackListener: TrackItemListener) :
    ListAdapter<ItemWithTracks, TrackAdapter.TrackViewHolder>(TrackAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(trackListener, item)
    }

    class TrackViewHolder private constructor(val binding: ListItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: TrackItemListener, item: ItemWithTracks) {
            binding.trackItem = item
            binding.trackListener = clickListener
            binding.executePendingBindings()
            handleWithStatus(item)
        }

        private fun handleWithStatus(item: ItemWithTracks) {
            when (val company = item.item.deliveryCompany) {
                DeliveryCompany.CORREIOS -> handleCorreiosStatus(item)
                else -> Timber.e("TrackViewHolder get a unknown delivery company: $company")
            }
        }

        private fun handleCorreiosStatus(item: ItemWithTracks) {
            val context = binding.root.context
            val lastStatus = item.tracks.first().status
            val status = CorreiosResourceStatus.getResourceStatus(lastStatus)
            binding.iconList.setImageResource(status.iconRes)
            val bgColor = ContextCompat.getColor(context, status.bgColorRes)
            binding.cardContainer.setBackgroundColor(bgColor)
            val titleColor = ContextCompat.getColor(context, status.titleColorRes)
            binding.labelTitleTrackItem.setTextColor(titleColor)
            val codeColor = ContextCompat.getColor(context, status.codeColorRes)
            binding.labelCode.setTextColor(codeColor)
            binding.labelStatus.setTextColor(codeColor)
            binding.labelObservation.setTextColor(codeColor)
            binding.labelLocale.setTextColor(codeColor)
            binding.labelForecast.setTextColor(codeColor)
        }

        companion object {
            fun from(parent: ViewGroup): TrackViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemTrackBinding.inflate(layoutInflater, parent, false)
                return TrackViewHolder(binding)
            }
        }
    }

    private companion object : DiffUtil.ItemCallback<ItemWithTracks>() {
        override fun areItemsTheSame(oldItem: ItemWithTracks, newItem: ItemWithTracks): Boolean {
            return oldItem.item.code == newItem.item.code
        }

        override fun areContentsTheSame(oldItem: ItemWithTracks, newItem: ItemWithTracks): Boolean {
            return oldItem == newItem
        }
    }
}
