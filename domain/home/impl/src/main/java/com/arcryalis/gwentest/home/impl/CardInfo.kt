package com.arcryalis.gwentest.home.impl

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardInfo(
    @PrimaryKey val id: String,
    val setId: String,
)