package dev.keader.correiostracker.database

import androidx.room.Embedded
import androidx.room.Relation

data class ItemWithTracks(
    @Embedded
    val item: Item,
    @Relation(
        parentColumn = "code",
        entityColumn = "itemCode"
    )
    val tracks: List<Track>
)