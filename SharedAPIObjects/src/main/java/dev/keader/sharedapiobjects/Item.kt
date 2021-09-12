package dev.keader.sharedapiobjects

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @PrimaryKey
    val code: String,
    var name: String,
    val type: String,
    val isDelivered: Boolean,
    val postedAt: String,
    val updatedAt: String,
    val isArchived: Boolean,
    var isWaitingPost: Boolean = false
)
