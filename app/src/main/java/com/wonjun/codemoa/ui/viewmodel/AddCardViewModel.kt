@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCardUiState())
    val uiState: StateFlow<AddCardUiState> = _uiState.asStateFlow()

    fun setInitialCardType(cardType: CardType) {
        _uiState.value = _uiState.value.copy(
            cardType = cardType,
            color = if (cardType == CardType.MEMBERSHIP) "#6750A4" else "#FF6B6B"
        )
    }

    fun updateCardType(cardType: CardType) {
        _uiState.value = _uiState.value.copy(
            cardType = cardType,
            color = if (cardType == CardType.MEMBERSHIP) "#6750A4" else "#FF6B6B"
        )
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(
            name = name,
            nameError = null
        )
    }

    fun updateCompany(company: String) {
        _uiState.value = _uiState.value.copy(
            company = company,
            companyError = null
        )
    }

    fun updateBarcodeData(barcodeData: String) {
        _uiState.value = _uiState.value.copy(
            barcodeData = barcodeData,
            barcodeDataError = null
        )
    }

    fun updateBarcodeType(barcodeType: BarcodeType) {
        _uiState.value = _uiState.value.copy(barcodeType = barcodeType)
    }

    fun updateColor(color: String) {
        _uiState.value = _uiState.value.copy(color = color)
    }

    // 멤버십 필드들
    fun updateMembershipNumber(membershipNumber: String) {
        _uiState.value = _uiState.value.copy(membershipNumber = membershipNumber)
    }

    fun updateBenefits(benefits: String) {
        _uiState.value = _uiState.value.copy(benefits = benefits)
    }

    fun updateTierLevel(tierLevel: String) {
        _uiState.value = _uiState.value.copy(tierLevel = tierLevel)
    }

    // 기프티콘 필드들
    fun updateValue(value: String) {
        _uiState.value = _uiState.value.copy(value = value)
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updateExpiryDate(expiryDate: Long) {
        _uiState.value = _uiState.value.copy(expiryDate = expiryDate)
    }

    fun saveCard(onResult: (Boolean) -> Unit) {
        if (!validateInput()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val card = createCard()
                cardRepository.insertCard(card)
                onResult(true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "카드 저장에 실패했습니다: ${e.message}"
                )
                onResult(false)
            }
        }
    }

    private fun validateInput(): Boolean {
        val state = _uiState.value
        var isValid = true

        val nameError = if (state.name.isBlank()) {
            isValid = false
            "카드명을 입력해주세요"
        } else null

        val companyError = if (state.company.isBlank()) {
            isValid = false
            "업체명을 입력해주세요"
        } else null

        val barcodeDataError = if (state.barcodeData.isBlank()) {
            isValid = false
            "바코드 데이터를 입력해주세요"
        } else null

        _uiState.value = state.copy(
            nameError = nameError,
            companyError = companyError,
            barcodeDataError = barcodeDataError
        )

        return isValid
    }

    private fun createCard(): Card {
        val state = _uiState.value

        return when (state.cardType) {
            CardType.MEMBERSHIP -> MembershipCard(
                name = state.name.trim(),
                company = state.company.trim(),
                barcodeData = state.barcodeData.trim(),
                barcodeType = state.barcodeType,
                color = state.color,
                membershipNumber = state.membershipNumber?.trim() ?: "",
                benefits = state.benefits?.trim() ?: "",
                tierLevel = state.tierLevel?.trim() ?: ""
            )
            CardType.GIFT_CARD -> GiftCard(
                name = state.name.trim(),
                company = state.company.trim(),
                barcodeData = state.barcodeData.trim(),
                barcodeType = state.barcodeType,
                color = state.color,
                expiryDate = state.expiryDate,
                value = state.value?.trim() ?: "",
                category = state.category?.trim() ?: ""
            )
        }
    }
}

data class AddCardUiState(
    val cardType: CardType = CardType.MEMBERSHIP,
    val name: String = "",
    val company: String = "",
    val barcodeData: String = "",
    val barcodeType: BarcodeType = BarcodeType.QR_CODE,
    val color: String = "#6750A4",

    // 멤버십 필드들
    val membershipNumber: String? = null,
    val benefits: String? = null,
    val tierLevel: String? = null,

    // 기프티콘 필드들
    val value: String? = null,
    val category: String? = null,
    val expiryDate: Long? = null,

    // UI 상태
    val isLoading: Boolean = false,
    val error: String? = null,

    // 검증 에러
    val nameError: String? = null,
    val companyError: String? = null,
    val barcodeDataError: String? = null
)