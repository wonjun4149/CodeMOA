package com.wonjun.codemoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wonjun.codemoa.data.model.Card
import com.wonjun.codemoa.data.repository.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            cardRepository.getAllCards().collect { cards ->
                _uiState.value = _uiState.value.copy(
                    cards = cards,
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

data class MainUiState(
    val cards: List<Card> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)