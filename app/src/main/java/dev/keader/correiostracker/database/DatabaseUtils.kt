package dev.keader.correiostracker.database

import dev.keader.correiosapi.Correios

fun Correios.CorreiosItem.toItemWithTracks() : ItemWithTracks {
    val item = Item(code, "", type, isDelivered, postedAt, updatedAt, false, isWaitingPost)
    val trackList = mutableListOf<Track>()

    tracks.forEach {
        trackList.add(Track(0, code, it.locale, it.status,
                it.observation, it.trackedAt, it.date, it.time, it.link))
    }

    return ItemWithTracks(item, trackList)
}
