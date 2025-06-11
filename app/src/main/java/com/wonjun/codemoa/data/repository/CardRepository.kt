package com.wonjun.codemoa.data.repository

import com.wonjun.codemoa.data.database.CardDao
import com.wonjun.codemoa.data.model.Card
import com.wonjun.codemoa.data.model.CardType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface CardRepository {
    fun getAllCards(): Flow<List<Card>>
    fun getFavoriteCards(): Flow<List<Card>>
    fun getCardsByType(cardType: CardType): Flow<List<Card>>
    suspend fun getCardById(id: String): Card?
    suspend fun getExpiringGiftCards(deadline: Long): List<Card>
    suspend fun insertCard(card: Card)
    suspend fun insertCards(cards: List<Card>)
    suspend fun updateCard(card: Card)
    suspend fun deleteCard(card: Card)
    suspend fun deleteAllCards()
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)
    suspend fun updateUsedStatus(id: String, isUsed: Boolean)
}

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao
) : CardRepository {

    override fun getAllCards(): Flow<List<Card>> = cardDao.getAllCards()

    override fun getFavoriteCards(): Flow<List<Card>> = cardDao.getFavoriteCards()

    override fun getCardsByType(cardType: CardType): Flow<List<Card>> =
        cardDao.getCardsByType(cardType)

    override suspend fun getCardById(id: String): Card? = cardDao.getCardById(id)

    override suspend fun getExpiringGiftCards(deadline: Long): List<Card> =
        cardDao.getExpiringGiftCards(deadline)

    override suspend fun insertCard(card: Card) = cardDao.insertCard(card)

    override suspend fun insertCards(cards: List<Card>) = cardDao.insertCards(cards)

    override suspend fun updateCard(card: Card) = cardDao.updateCard(card)

    override suspend fun deleteCard(card: Card) = cardDao.deleteCard(card)

    override suspend fun deleteAllCards() = cardDao.deleteAllCards()

    override suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) =
        cardDao.updateFavoriteStatus(id, isFavorite)

    override suspend fun updateUsedStatus(id: String, isUsed: Boolean) =
        cardDao.updateUsedStatus(id, isUsed)
}