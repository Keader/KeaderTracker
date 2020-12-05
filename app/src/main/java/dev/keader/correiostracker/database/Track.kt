package dev.keader.correiostracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey(autoGenerate = true)
    val trackUid: Long,
    val itemCode: String,
    val locale: String,
    val status: String,
    val observation: String,
    val trackedAt: String,
    val date: String,
    val time: String
)

data class TrackWithStatus(
    val track: Track,
    val delivered: Boolean,
    val isWaitingPost: Boolean
)
