package dev.keader.correiostracker.view.interfaces

import dev.keader.sharedapiobjects.ItemWithTracks

interface TrackHistoryListener {
    fun onItemClicked(item: ItemWithTracks, id: TrackHistoryButtonTypes)
}

enum class TrackHistoryButtonTypes {
    BUTTON_BACK,
    BUTTON_COPY,
    BUTTON_DELETE,
    BUTTON_SHARE,
    BUTTON_EDIT
}
