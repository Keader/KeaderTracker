package dev.keader.correiostracker.view.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.Track
import java.text.SimpleDateFormat

@BindingAdapter("trackImage")
fun ImageView.setTrackIcon(item: ItemWithTracks) {
    setImageResource(when (item.item.isDelivered) {
        true -> R.drawable.truck
        false -> R.drawable.box
    })
}

@BindingAdapter("trackDateText")
fun TextView.setDateText(item: ItemWithTracks) {
    text = item.tracks.first().date
}

@BindingAdapter("trackTimeText")
fun TextView.setTimeText(item: ItemWithTracks) {
    text = item.tracks.first().time
}

@BindingAdapter("trackStatus")
fun TextView.setTrackStatus(item: ItemWithTracks) {
    text = item.tracks.first().status
}

@BindingAdapter("trackObservation")
fun TextView.setTrackObservation(item: ItemWithTracks) {
    val observation = item.tracks.first().observation
    if (observation.isNotEmpty())
        text = observation
    else
        visibility = View.GONE
}

@BindingAdapter("trackLocale")
fun TextView.setTrackLocale(item: ItemWithTracks) {
    text = item.tracks.first().locale
}

@BindingAdapter("daysSpend")
fun TextView.setDaysSpend(item: ItemWithTracks) {
    val startDate = item.tracks.last().date
    val lastDate = item.tracks.first().date
    val dates = SimpleDateFormat("dd/mm/yyyy")
    val date1 = dates.parse(startDate)
    val date2 = dates.parse(lastDate)
    if (date1 == null || date2 == null)
        return

    val difference: Long = kotlin.math.abs(date1.time - date2.time)
    val differenceDates = difference / (24 * 60 * 60 * 1000)
    text = context.resources.getQuantityString(R.plurals.duration_days, differenceDates.toInt(), differenceDates.toString())
}

@BindingAdapter("observation")
fun TextView.setObservation(track: Track) {
    if (track.observation.isEmpty())
        visibility = View.GONE
    else
        text = track.observation
}
