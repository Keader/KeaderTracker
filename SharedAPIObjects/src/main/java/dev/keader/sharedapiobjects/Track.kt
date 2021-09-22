package dev.keader.sharedapiobjects

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["itemCode"])])
data class Track(
    @PrimaryKey(autoGenerate = true)
    val trackUid: Long,
    val itemCode: String,
    val locale: String,
    val status: String,
    val observation: String,
    val trackedAt: String,
    val date: String,
    val time: String,
    val link: String
    // TODO: Missing add Icon ref here
)

data class TrackWithStatus(
    val track: Track,
    val delivered: Boolean,
    val isWaitingPost: Boolean
)
