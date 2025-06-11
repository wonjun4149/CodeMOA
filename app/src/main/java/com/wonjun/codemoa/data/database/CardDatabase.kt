package com.wonjun.codemoa.data.database

import androidx.room.*
import androidx.room.Database
import com.wonjun.codemoa.data.model.Card
import com.wonjun.codemoa.data.model.CardType
import com.wonjun.codemoa.data.model.BarcodeType
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY createdAt DESC")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteCards(): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE cardType = :cardType ORDER BY createdAt DESC")
    fun getCardsByType(cardType: CardType): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: String): Card?

    @Query("SELECT * FROM cards WHERE cardType = 'GIFT_CARD' AND expiryDate IS NOT NULL AND expiryDate <= :deadline AND isUsed = 0")
    suspend fun getExpiringGiftCards(deadline: Long): List<Card>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<Card>)

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)

    @Query("DELETE FROM cards")
    suspend fun deleteAllCards()

    @Query("UPDATE cards SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("UPDATE cards SET isUsed = :isUsed WHERE id = :id")
    suspend fun updateUsedStatus(id: String, isUsed: Boolean)
}

// 타입 변환기
class Converters {
    @TypeConverter
    fun fromCardType(cardType: CardType): String {
        return cardType.name
    }

    @TypeConverter
    fun toCardType(cardType: String): CardType {
        return CardType.valueOf(cardType)
    }

    @TypeConverter
    fun fromBarcodeType(barcodeType: BarcodeType): String {
        return barcodeType.name
    }

    @TypeConverter
    fun toBarcodeType(barcodeType: String): BarcodeType {
        return BarcodeType.valueOf(barcodeType)
    }
}

@Database(
    entities = [Card::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

    companion object {
        @Volatile
        private var INSTANCE: CardDatabase? = null

        fun getDatabase(context: android.content.Context): CardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CardDatabase::class.java,
                    "card_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}