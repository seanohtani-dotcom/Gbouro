package com.animegallery.app.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.usecase.GetAllFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the favorites screen
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            getAllFavoritesUseCase()
                .collect { favorites ->
                    _uiState.update {
                        it.copy(
                            favorites = favorites,
                            isLoading = false,
                            isEmpty = favorites.isEmpty()
                        )
                    }
                }
        }
    }
}

/**
 * UI state for the favorites screen
 */
data class FavoritesUiState(
    val favorites: List<ImageItem> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
