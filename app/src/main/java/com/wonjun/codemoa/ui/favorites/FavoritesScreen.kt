package com.wonjun.codemoa.ui.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wonjun.codemoa.R
import com.wonjun.codemoa.ui.components.CardItem
import com.wonjun.codemoa.ui.components.EmptyStateCard
import com.wonjun.codemoa.ui.components.LoadingCard
import com.wonjun.codemoa.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.tab_favorites),
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
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(6) { // 스켈레톤 로딩 카드 6개
                        LoadingCard()
                    }
                }
            }

            uiState.favoriteCards.isEmpty() -> {
                // 즐겨찾기가 비어있을 때
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateCard(
                        title = "즐겨찾기가 비어있습니다",
                        description = stringResource(R.string.message_no_favorites),
                        icon = Icons.Default.Favorite
                    )
                }
            }

            else -> {
                // 즐겨찾기 카드 목록
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = uiState.favoriteCards,
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