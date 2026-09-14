package com.arcryalis.gwentest.data.local.api.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardInfoEntity(
    @PrimaryKey val id: String,
    val setId: String,
    val name: String,
    val smallImageUrl: String,
    val oracleText: String,
    val isOngoing: Boolean
)