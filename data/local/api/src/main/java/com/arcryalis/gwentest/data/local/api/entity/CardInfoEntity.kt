package com.arcryalis.gwentest.data.local.api.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cardInfo",
    foreignKeys = [
        ForeignKey(
            entity = CardSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CardInfoEntity(
    @PrimaryKey val id: String,
    val setId: String,
    val name: String,
    val smallImageUrl: String,
    val largeImageUrl: String,
    val oracleText: String?,
    val isOngoing: Boolean
)