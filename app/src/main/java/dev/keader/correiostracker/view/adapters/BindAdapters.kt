package dev.keader.correiostracker.view.adapters

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import dev.keader.correiostracker.R
import dev.keader.correiostracker.database.Item
import dev.keader.correiostracker.database.ItemWithTracks
import dev.keader.correiostracker.database.Track
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@BindingAdapter("trackImage")
fun ImageView.setTrackIcon(item: ItemWithTracks) {
    setImageResource(when (item.item.isDelivered) {
        true -> R.drawable.box2
        false -> R.drawable.truck2
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
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val lastUpdate = LocalDate.parse(item.tracks.first().date, formatter)
    val today = LocalDateTime.now()
    val difference = lastUpdate.until(today, ChronoUnit.DAYS)

    text = if (difference >= 7 && item.item.isDelivered) {
        context.getString(R.string.produto_estagnado)
    } else {
        item.tracks.first().status
    }
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
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val startDate = LocalDate.parse(item.tracks.last().date, formatter)
    val endDate = LocalDateTime.now()
    var difference = startDate.until(endDate, ChronoUnit.DAYS)

    if (item.item.isDelivered) {
        val deliveryDate = LocalDate.parse(item.tracks.first().date, formatter)
        difference = startDate.until(deliveryDate, ChronoUnit.DAYS)
    }

    text = context.resources.getQuantityString(R.plurals.duration_days, difference.toInt(), difference.toString())
}

@BindingAdapter("observation")
fun TextView.setObservation(track: Track) {
    if (track.observation.isEmpty())
        visibility = View.GONE
    else
        text = track.observation
}

@BindingAdapter("setLink")
fun TextView.setLink(track: Track) {
    if (track.link.isNotEmpty()) {
        val string = if (track.link.contains("prazo"))
            context.getString(R.string.imports)
        else
            context.getString(R.string.deadlines)
        val link = "<a href=\"${track.link}\">${string}</a>"
        val spannable = HtmlCompat.fromHtml(link,0)
        movementMethod = LinkMovementMethod.getInstance()
        text = spannable
        visibility = View.VISIBLE
    }
}

@BindingAdapter("setType")
fun TextView.setType(item: Item) {
    val locale = Locale.getDefault()
    text = item.type.toLowerCase(locale).capitalize(locale)
}
