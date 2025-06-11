@HiltViewModel
class GiftCardViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GiftCardUiState())
    val uiState: StateFlow<GiftCardUiState> = _uiState.asStateFlow()

    init {
        loadGiftCards()
    }

    private fun loadGiftCards() {
        viewModelScope.launch {
            cardRepository.getCardsByType(com.wonjun.codemoa.data.model.CardType.GIFT_CARD).collect { cards ->
                _uiState.value = _uiState.value.copy(
                    giftCards = cards,
                    isLoading = false
                )
            }
        }
    }

    fun toggleFavorite(cardId: String) {
        viewModelScope.launch {
            val card = cardRepository.getCardById(cardId)
            if (card != null) {
                cardRepository.updateFavoriteStatus(cardId, !card.isFavorite)
            }
        }
    }

    fun toggleUsedStatus(cardId: String) {
        viewModelScope.launch {
            val card = cardRepository.getCardById(cardId)
            if (card != null && card.cardType == com.wonjun.codemoa.data.model.CardType.GIFT_CARD) {
                val giftCard = card as com.wonjun.codemoa.data.model.GiftCard
                cardRepository.updateUsedStatus(cardId, !giftCard.isUsed)
            }
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            cardRepository.deleteCard(card)
        }
    }
}

data class GiftCardUiState(
    val giftCards: List<Card> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)