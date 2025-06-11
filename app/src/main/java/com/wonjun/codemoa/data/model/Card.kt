package com.wonjun.codemoa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
enum class CardType {
    MEMBERSHIP, GIFT_CARD
}

@Serializable
enum class BarcodeType {
    QR_CODE, CODE_128, CODE_39, EAN_13, EAN_8
}

@Entity(tableName = "cards")
@Serializable
data class Card(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val company: String,
    val barcodeData: String,
    val barcodeType: BarcodeType,
    val cardType: CardType,
    val color: String = if (cardType == CardType.MEMBERSHIP) "#6750A4" else "#FF6B6B",
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,

    // 멤버십 전용 필드
    val membershipNumber: String? = null,
    val benefits: String? = null,
    val tierLevel: String? = null,

    // 기프티콘 전용 필드
    val expiryDate: Long? = null,
    val value: String? = null,
    val category: String? = null,
    val isUsed: Boolean = false
) {
    // 기프티콘 만료일까지 남은 일수 계산
    fun getDaysUntilExpiry(): Int? {
        return expiryDate?.let { expiry ->
            val currentTime = System.currentTimeMillis()
            val diffInMillis = expiry - currentTime
            (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        }
    }

    // 만료 임박 여부 확인
    fun isExpiringSoon(daysThreshold: Int = 7): Boolean {
        val daysUntilExpiry = getDaysUntilExpiry()
        return daysUntilExpiry != null && daysUntilExpiry <= daysThreshold && daysUntilExpiry >= 0
    }

    // 만료 여부 확인
    fun isExpired(): Boolean {
        val daysUntilExpiry = getDaysUntilExpiry()
        return daysUntilExpiry != null && daysUntilExpiry < 0
    }
}

// 백업 데이터 구조
@Serializable
data class BackupData(
    val version: String = "1.0",
    val createdAt: Long = System.currentTimeMillis(),
    val cards: List<Card>,
    val settings: AppSettings,
    val passwordHash: String
)

@Serializable
data class AppSettings(
    val themeMode: Int = 0, // 0: System, 1: Light, 2: Dark
    val languageCode: String = "system", // "ko", "en", "ja", "system"
    val notificationEnabled: Boolean = true,
    val expiryNotificationEnabled: Boolean = true,
    val expiryNotificationDays: Int = 7,
    val appLockEnabled: Boolean = false,
    val biometricEnabled: Boolean = false
)