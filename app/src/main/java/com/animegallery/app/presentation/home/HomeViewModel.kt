package com.animegallery.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.usecase.GetImagesUseCase
import com.animegallery.app.domain.usecase.GetTagSuggestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getImagesUseCase: GetImagesUseCase,
    private val getTagSuggestionsUseCase: GetTagSuggestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _images = MutableStateFlow<PagingData<ImageItem>>(PagingData.empty())
    val images: StateFlow<PagingData<ImageItem>> = _images.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadImages()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        
        // Debounce tag suggestions
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Wait 300ms before fetching suggestions
            fetchTagSuggestions(query)
        }
    }

    fun onSearchSubmit() {
        _uiState.update { it.copy(tagSuggestions = emptyList()) }
        loadImages()
    }

    fun onTagSelected(tag: String) {
        val currentQuery = _uiState.value.searchQuery
        val newQuery = if (currentQuery.isEmpty()) {
            tag
        } else {
            "$currentQuery $tag"
        }
        _uiState.update { it.copy(searchQuery = newQuery, tagSuggestions = emptyList()) }
        loadImages()
    }

    fun onRetry() {
        loadImages()
    }

    private fun fetchTagSuggestions(query: String) {
        viewModelScope.launch {
            // Get the last incomplete tag
            val lastTag = query.split(" ").lastOrNull()?.trim() ?: ""
            
            if (lastTag.length >= 2) {
                getTagSuggestionsUseCase(lastTag)
                    .onSuccess { suggestions ->
                        _uiState.update { it.copy(tagSuggestions = suggestions) }
                    }
                    .onFailure {
                        _uiState.update { it.copy(tagSuggestions = emptyList()) }
                    }
            } else {
                _uiState.update { it.copy(tagSuggestions = emptyList()) }
            }
        }
    }

    private fun loadImages() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val tags = _uiState.value.searchQuery
                    .split(" ")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                getImagesUseCase(tags)
                    .cachedIn(viewModelScope)
                    .collect { pagingData ->
                        _images.value = pagingData
                        _uiState.update { it.copy(isLoading = false) }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }
}

/**
 * UI state for the home screen
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val tagSuggestions: List<String> = emptyList()
)
