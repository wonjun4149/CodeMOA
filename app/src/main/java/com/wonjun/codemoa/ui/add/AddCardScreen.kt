package com.wonjun.codemoa.ui.add

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.wonjun.codemoa.R
import com.wonjun.codemoa.data.model.BarcodeType
import com.wonjun.codemoa.data.model.Card
import com.wonjun.codemoa.data.model.CardType
import com.wonjun.codemoa.data.model.GiftCard
import com.wonjun.codemoa.data.model.MembershipCard
import com.wonjun.codemoa.ui.viewmodel.AddCardViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    navController: NavController,
    initialCardType: CardType = CardType.MEMBERSHIP,
    viewModel: AddCardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

    // 날짜 선택 다이얼로그
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            viewModel.updateExpiryDate(calendar.timeInMillis)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() // 오늘 이후만 선택 가능
    }

    LaunchedEffect(initialCardType) {
        viewModel.setInitialCardType(initialCardType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_card_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    // 바코드 스캔 버튼
                    IconButton(
                        onClick = {
                            // TODO: 바코드 스캔 화면으로 이동
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(R.string.action_scan)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 카드 타입 선택 탭
            TabRow(
                selectedTabIndex = if (uiState.cardType == CardType.MEMBERSHIP) 0 else 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = uiState.cardType == CardType.MEMBERSHIP,
                    onClick = { viewModel.updateCardType(CardType.MEMBERSHIP) },
                    text = { Text(stringResource(R.string.tab_membership)) }
                )
                Tab(
                    selected = uiState.cardType == CardType.GIFT_CARD,
                    onClick = { viewModel.updateCardType(CardType.GIFT_CARD) },
                    text = { Text(stringResource(R.string.tab_gift_card)) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 기본 정보 입력
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "기본 정보",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 카드명
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        label = { Text(stringResource(R.string.card_name)) },
                        placeholder = { Text("예: 스타벅스 카드") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.nameError != null,
                        supportingText = uiState.nameError?.let { { Text(it) } }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 업체명
                    OutlinedTextField(
                        value = uiState.company,
                        onValueChange = viewModel::updateCompany,
                        label = { Text(stringResource(R.string.company_name)) },
                        placeholder = { Text("예: 스타벅스 코리아") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.companyError != null,
                        supportingText = uiState.companyError?.let { { Text(it) } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 바코드 정보
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "바코드 정보",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedButton(
                            onClick = {
                                // TODO: 바코드 스캔 기능
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.action_scan))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 바코드 타입 선택
                    Text(
                        text = stringResource(R.string.barcode_type),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    BarcodeTypeSelector(
                        selectedType = uiState.barcodeType,
                        onTypeSelected = viewModel::updateBarcodeType
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 바코드 데이터
                    OutlinedTextField(
                        value = uiState.barcodeData,
                        onValueChange = viewModel::updateBarcodeData,
                        label = { Text(stringResource(R.string.barcode_data)) },
                        placeholder = { Text("바코드 번호를 입력하세요") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.barcodeDataError != null,
                        supportingText = uiState.barcodeDataError?.let { { Text(it) } }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 카드 타입별 추가 정보
            when (uiState.cardType) {
                CardType.MEMBERSHIP -> {
                    MembershipFields(
                        membershipNumber = uiState.membershipNumber ?: "",
                        benefits = uiState.benefits ?: "",
                        tierLevel = uiState.tierLevel ?: "",
                        onMembershipNumberChange = viewModel::updateMembershipNumber,
                        onBenefitsChange = viewModel::updateBenefits,
                        onTierLevelChange = viewModel::updateTierLevel
                    )
                }
                CardType.GIFT_CARD -> {
                    GiftCardFields(
                        value = uiState.value ?: "",
                        category = uiState.category ?: "",
                        expiryDate = uiState.expiryDate,
                        onValueChange = viewModel::updateValue,
                        onCategoryChange = viewModel::updateCategory,
                        onExpiryDateClick = { datePickerDialog.show() },
                        dateFormat = dateFormat
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 카드 색상 선택
            CardColorSelector(
                selectedColor = uiState.color,
                onColorSelected = viewModel::updateColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 저장 버튼
            Button(
                onClick = {
                    viewModel.saveCard { success ->
                        if (success) {
                            navController.navigateUp()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.action_save),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // 에러 처리
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // TODO: 스낵바로 에러 메시지 표시
        }
    }
}

@Composable
private fun BarcodeTypeSelector(
    selectedType: BarcodeType,
    onTypeSelected: (BarcodeType) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(BarcodeType.values()) { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                label = {
                    Text(
                        text = when (type) {
                            BarcodeType.QR_CODE -> stringResource(R.string.barcode_qr)
                            BarcodeType.CODE_128 -> stringResource(R.string.barcode_code128)
                            BarcodeType.CODE_39 -> stringResource(R.string.barcode_code39)
                            BarcodeType.EAN_13 -> stringResource(R.string.barcode_ean13)
                            BarcodeType.EAN_8 -> stringResource(R.string.barcode_ean8)
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun MembershipFields(
    membershipNumber: String,
    benefits: String,
    tierLevel: String,
    onMembershipNumberChange: (String) -> Unit,
    onBenefitsChange: (String) -> Unit,
    onTierLevelChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "멤버십 정보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = membershipNumber,
                onValueChange = onMembershipNumberChange,
                label = { Text(stringResource(R.string.membership_number)) },
                placeholder = { Text("멤버십 번호 (선택사항)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = benefits,
                onValueChange = onBenefitsChange,
                label = { Text(stringResource(R.string.benefits)) },
                placeholder = { Text("혜택 내용 (선택사항)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tierLevel,
                onValueChange = onTierLevelChange,
                label = { Text(stringResource(R.string.tier_level)) },
                placeholder = { Text("등급 (선택사항)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GiftCardFields(
    value: String,
    category: String,
    expiryDate: Long?,
    onValueChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onExpiryDateClick: () -> Unit,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "기프티콘 정보",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(stringResource(R.string.gift_value)) },
                placeholder = { Text("금액 (선택사항)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = category,
                onValueChange = onCategoryChange,
                label = { Text(stringResource(R.string.category)) },
                placeholder = { Text("카테고리 (선택사항)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 만료일 선택
            OutlinedTextField(
                value = expiryDate?.let { dateFormat.format(Date(it)) } ?: "",
                onValueChange = { },
                label = { Text(stringResource(R.string.expiry_date)) },
                placeholder = { Text(stringResource(R.string.select_expiry_date)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpiryDateClick() },
                enabled = false,
                trailingIcon = {
                    IconButton(onClick = onExpiryDateClick) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.select_expiry_date)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun CardColorSelector(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "카드 색상",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val colors = listOf(
                "#6750A4", "#FF6B6B", "#4CAF50", "#2196F3",
                "#FF9800", "#9C27B0", "#F44336", "#795548"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(colors) { color ->
                    ColorOption(
                        color = color,
                        isSelected = selectedColor == color,
                        onSelected = { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorOption(
    color: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                try {
                    Color(android.graphics.Color.parseColor(color))
                } catch (e: IllegalArgumentException) {
                    MaterialTheme.colorScheme.primary
                }
            )
            .clickable { onSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}