package dev.keader.correiostracker.view.adapters

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import dev.keader.correiostracker.R
import dev.keader.correiostracker.model.toCapitalize
import dev.keader.sharedapiobjects.Item
import dev.keader.sharedapiobjects.ItemWithTracks
import dev.keader.sharedapiobjects.Track
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

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
    val alternativeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val lastUpdate: LocalDate = try {
        LocalDate.parse(item.tracks.first().date, formatter)
    } catch (e: Exception) {
        LocalDate.parse(item.tracks.first().date, alternativeFormatter)
    }
    val today = LocalDateTime.now()
    val difference = lastUpdate.until(today, ChronoUnit.DAYS)

    text = if (difference >= 7 && !item.item.isDelivered) {
        val jokes = resources.getStringArray(R.array.alternative_product_state_stagnant).toList()
        jokes.random()
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

@BindingAdapter("deliveryPrediction")
fun TextView.setDeliveryPrediction(item: ItemWithTracks) {
    val forecast = item.item.deliveryForecast
    if (forecast.isEmpty())
        visibility = View.GONE
    else
        text = context.getString(R.string.delivery_forecast_format, forecast)
}

@BindingAdapter("daysSpend")
fun TextView.setDaysSpend(item: ItemWithTracks) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val alternativeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    var startDate: LocalDate
    try {
        startDate = LocalDate.parse(item.tracks.last().date, formatter)
    } catch(e: Exception) {
        startDate = LocalDate.parse(item.tracks.last().date, alternativeFormatter)
    }
    val endDate = LocalDateTime.now()
    var difference = startDate.until(endDate, ChronoUnit.DAYS)

    if (item.item.isDelivered) {
        var deliveryDate: LocalDate
        try {
            deliveryDate = LocalDate.parse(item.tracks.first().date, formatter)
        }catch (e: Exception) {
            deliveryDate = LocalDate.parse(item.tracks.first().date, alternativeFormatter)
        }
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
            context.getString(R.string.deadlines)
        else
            context.getString(R.string.imports)
        val link = "<a href=\"${track.link}\">${string}</a>"
        val spannable = HtmlCompat.fromHtml(link,0)
        movementMethod = LinkMovementMethod.getInstance()
        text = spannable
        visibility = View.VISIBLE
    }
}

@BindingAdapter("setLink")
fun TextView.setLink(link: String) {
    val myText = link.replace("https://", "")
    var name = if (myText.contains("github"))
        "Github"
    else
        "Linkedin"
    val clickable = "<a href=\"${link}\">${name}</a>"
    val spannable = HtmlCompat.fromHtml(clickable, 0)
    movementMethod = LinkMovementMethod.getInstance()
    text = spannable
}

@BindingAdapter("setContribution")
fun TextView.setContribution(count: Int) {
    text = context.getString(R.string.contribute_format, count)
}

@BindingAdapter("setAvatar")
fun ImageView.setAvatar(link: String) {
    Glide.with(context)
        .load(link)
        .transform(CenterCrop(), RoundedCorners(16))
        .placeholder(R.drawable.ic_loading)
        .into(this)
}

@BindingAdapter("setType")
fun TextView.setType(item: Item) {
    val locale = Locale.getDefault()
    text = item.type.lowercase(locale).toCapitalize()
}
