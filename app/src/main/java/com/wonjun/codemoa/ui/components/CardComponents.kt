package com.wonjun.codemoa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wonjun.codemoa.R
import com.wonjun.codemoa.data.model.Card
import com.wonjun.codemoa.data.model.CardType
import com.wonjun.codemoa.data.model.GiftCard
import com.wonjun.codemoa.ui.theme.MembershipCardColor
import com.wonjun.codemoa.ui.theme.GiftCardColor
import com.wonjun.codemoa.ui.theme.ExpiredColor
import com.wonjun.codemoa.ui.theme.ErrorColor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardItem(
    card: Card,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = try {
                Color(android.graphics.Color.parseColor(card.color))
            } catch (e: IllegalArgumentException) {
                if (card.cardType == CardType.MEMBERSHIP) MembershipCardColor else GiftCardColor
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 헤더 (카드 타입 + 즐겨찾기)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getCardTypeIcon(card.cardType),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = getCardTypeText(card.cardType),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (card.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.cd_favorite_card),
                        tint = if (card.isFavorite) Color.Red else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 카드 이름
            Text(
                text = card.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 업체명
            Text(
                text = card.company,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 기프티콘인 경우 만료일 표시
            if (card is GiftCard && card.expiryDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ExpiryDateChip(giftCard = card)
            }

            // 멤버십인 경우 등급 표시
            if (card.cardType == CardType.MEMBERSHIP && !card.tierLevel.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                TierLevelChip(tierLevel = card.tierLevel!!)
            }
        }
    }
}

@Composable
private fun ExpiryDateChip(giftCard: GiftCard) {
    val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
    val daysUntilExpiry = giftCard.getDaysUntilExpiry()

    val (backgroundColor, textColor, text) = when {
        giftCard.isUsed -> Triple(
            ExpiredColor.copy(alpha = 0.8f),
            Color.White,
            stringResource(R.string.used)
        )
        giftCard.isExpired() -> Triple(
            ErrorColor.copy(alpha = 0.8f),
            Color.White,
            stringResource(R.string.expired)
        )
        giftCard.isExpiringSoon() -> Triple(
            Color.Orange.copy(alpha = 0.8f),
            Color.White,
            stringResource(R.string.expires_in_days, daysUntilExpiry ?: 0)
        )
        else -> Triple(
            Color.White.copy(alpha = 0.2f),
            Color.White,
            dateFormat.format(Date(giftCard.expiryDate!!))
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TierLevelChip(tierLevel: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = tierLevel,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun getCardTypeIcon(cardType: CardType): ImageVector {
    return when (cardType) {
        CardType.MEMBERSHIP -> Icons.Default.CreditCard
        CardType.GIFT_CARD -> Icons.Default.CardGiftcard
    }
}

@Composable
private fun getCardTypeText(cardType: CardType): String {
    return when (cardType) {
        CardType.MEMBERSHIP -> stringResource(R.string.tab_membership)
        CardType.GIFT_CARD -> stringResource(R.string.tab_gift_card)
    }
}

@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Add,
    onActionClick: (() -> Unit)? = null,
    actionText: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (onActionClick != null && actionText != null) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onActionClick
                ) {
                    Text(text = actionText)
                }
            }
        }
    }
}

@Composable
fun LoadingCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 스켈레톤 로딩 효과
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            )
        }
    }
}