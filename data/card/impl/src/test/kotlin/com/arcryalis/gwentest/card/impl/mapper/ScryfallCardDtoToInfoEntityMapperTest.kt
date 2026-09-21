package com.arcryalis.gwentest.card.impl.mapper

import com.arcryalis.gwentest.data.local.api.TestCardInfoEntity
import com.arcryalis.gwentest.data.local.api.entity.CardInfoEntity
import com.arcryalis.gwentest.remote.scryfall.TestScryfallDataDto
import org.junit.Test
import kotlin.test.assertEquals

class ScryfallCardDtoToInfoEntityMapperTest {

    @Test
    fun givenScryfallCardDtoWithOngoingType_whenToInfoEntity_thenIsOngoingTrue() {
        val dto = TestScryfallDataDto.dto1.copy(
            type = "Ongoing"
        )
        val expectedEntity = CardInfoEntity(
            id = dto.id,
            setId = dto.setId,
            name = dto.name,
            smallImageUrl = dto.imageUris!!.small,
            largeImageUrl = dto.imageUris!!.large,
            oracleText = dto.oracleText,
            isOngoing = true
        )

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtoWithoutOngoingType_whenToInfoEntity_thenIsOngoingFalse() {
        val dto = TestScryfallDataDto.dto2.copy(
            type = "Other"
        )
        val expectedEntity = CardInfoEntity(
            id = dto.id,
            setId = dto.setId,
            name = dto.name,
            smallImageUrl = dto.imageUris!!.small,
            largeImageUrl = dto.imageUris!!.large,
            oracleText = dto.oracleText,
            isOngoing = false
        )
        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtoWithoutImageUris_whenToInfoEntity_thenEmptyImageUrls() {
        val dto = TestScryfallDataDto.dto1.copy(
            type = "Other",
            imageUris = null
        )
        val expectedEntity = CardInfoEntity(
            id = dto.id,
            setId = dto.setId,
            name = dto.name,
            smallImageUrl = "",
            largeImageUrl = "",
            oracleText = dto.oracleText,
            isOngoing = false
        )

        val result = dto.toInfoEntity()

        assertEquals(expectedEntity, result)
    }

    @Test
    fun givenScryfallCardDtos_whenToInfoEntityList_thenReturnsCardInfoEntities() {
        val dto = TestScryfallDataDto.dto1.copy(
            type = "Other"
        )
        val dto2 = TestScryfallDataDto.dto2.copy(
            type = "Scheme - ongoing"
        )
        val expectedEntities = listOf(
            CardInfoEntity(
                id = dto.id,
                setId = dto.setId,
                name = dto.name,
                smallImageUrl = dto.imageUris!!.small,
                largeImageUrl = dto.imageUris!!.large,
                oracleText = dto.oracleText,
                isOngoing = false
            ),
            CardInfoEntity(
                id = dto2.id,
                setId = dto2.setId,
                name = dto2.name,
                smallImageUrl = dto2.imageUris!!.small,
                largeImageUrl = dto2.imageUris!!.large,
                oracleText = dto2.oracleText,
                isOngoing = true
            )
        )

        val result = listOf(dto, dto2).toInfoEntity()

        assertEquals(expectedEntities, result)
    }
}