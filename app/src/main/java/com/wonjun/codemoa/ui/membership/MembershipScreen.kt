package com.wonjun.codemoa.ui.membership

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
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
import com.wonjun.codemoa.ui.viewmodel.MembershipViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembershipScreen(
    navController: NavController,
    viewModel: MembershipViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: 멤버십 추가 화면으로 이동
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
                text = stringResource(R.string.tab_membership),
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

                uiState.membershipCards.isEmpty() -> {
                    // 멤버십이 비어있을 때
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyStateCard(
                            title = "등록된 멤버십이 없습니다",
                            description = stringResource(R.string.message_no_membership),
                            icon = Icons.Default.CreditCard,
                            actionText = stringResource(R.string.add_membership_title),
                            onActionClick = {
                                // TODO: 멤버십 추가 화면으로 이동
                            }
                        )
                    }
                }

                else -> {
                    // 멤버십 카드 목록
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp) // FAB를 위한 여백
                    ) {
                        items(
                            items = uiState.membershipCards,
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