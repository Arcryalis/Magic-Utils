package com.arcryalis.gwentest.data.local.api.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cardSets")
data class CardSetEntity(
    @PrimaryKey val id: String,
    val name: String
)