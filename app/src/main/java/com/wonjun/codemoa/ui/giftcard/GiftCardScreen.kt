package com.wonjun.codemoa.ui.giftcard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wonjun.codemoa.R
import com.wonjun.codemoa.ui.components.CardItem
import com.wonjun.codemoa.ui.components.EmptyStateCard
import com.wonjun.codemoa.ui.components.LoadingCard
import com.wonjun.codemoa.ui.viewmodel.GiftCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftCardScreen(
    navController: NavController,
    viewModel: GiftCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: 기프티콘 추가 화면으로 이동
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_add_card)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.tab_gift_card),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when {
                uiState.isLoading -> {
                    // 로딩 상태
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp) // FAB를 위한 여백
                    ) {
                        items(6) {
                            LoadingCard()
                        }
                    }
                }

                uiState.giftCards.isEmpty() -> {
                    // 기프티콘이 비어있을 때
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateCard(
                            title = "등록된 기프티콘이 없습니다",
                            description = stringResource(R.string.message_no_gift_cards),
                            icon = Icons.Default.CardGiftcard,
                            actionText = stringResource(R.string.add_gift_card_title),
                            onActionClick = {
                                // TODO: 기프티콘 추가 화면으로 이동
                            }
                        )
                    }
                }

                else -> {
                    // 기프티콘 카드 목록
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp) // FAB를 위한 여백
                    ) {
                        items(
                            items = uiState.giftCards,
                            key = { card -> card.id }
                        ) { card ->
                            CardItem(
                                card = card,
                                onClick = {
                                    // TODO: 카드 상세보기 화면으로 이동
                                },
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(card.id)
                                }
                            )
                        }
                    }
                }
            }

            // 에러 처리
            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    // TODO: 스낵바로 에러 메시지 표시
                }
            }
        }
    }
}