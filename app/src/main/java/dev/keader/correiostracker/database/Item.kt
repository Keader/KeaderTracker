package dev.keader.correiostracker.database

import androidx.room.*

@Entity
data class Item(
    @PrimaryKey
    val code: String,
    var name: String,
    val type: String,
    val isDelivered: Boolean,
    val postedAt: String,
    val updatedAt: String,
    val isArchived: Boolean
)
