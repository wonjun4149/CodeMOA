@HiltViewModel
class MembershipViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MembershipUiState())
    val uiState: StateFlow<MembershipUiState> = _uiState.asStateFlow()

    init {
        loadMembershipCards()
    }

    private fun loadMembershipCards() {
        viewModelScope.launch {
            cardRepository.getCardsByType(com.wonjun.codemoa.data.model.CardType.MEMBERSHIP).collect { cards ->
                _uiState.value = _uiState.value.copy(
                    membershipCards = cards,
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

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            cardRepository.deleteCard(card)
        }
    }
}

data class MembershipUiState(
    val membershipCards: List<Card> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)