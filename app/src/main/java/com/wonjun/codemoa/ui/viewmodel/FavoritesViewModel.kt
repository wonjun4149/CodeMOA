@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteCards()
    }

    private fun loadFavoriteCards() {
        viewModelScope.launch {
            cardRepository.getFavoriteCards().collect { cards ->
                _uiState.value = _uiState.value.copy(
                    favoriteCards = cards,
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
}

data class FavoritesUiState(
    val favoriteCards: List<Card> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)