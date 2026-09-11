package com.arcryalis.gwentest.card.impl

class CardRepositoryImpl(
    private val db: CardDataStore,
//    private val api: ScryfallApi
) : CardRepository {

    override suspend fun downloadSet(id: String) {
        //retrieve values

        val result = emptyList<CardInfo>()

        db.updateSet(result)

    }

    override fun isSetAvailable(id: String): Flow<Boolean> {
        //check available

        return db.getSetExists(id)
    }

    override fun getSet(setId: String): Flow<List<CardInfo>> = db.getSet(setId)

}