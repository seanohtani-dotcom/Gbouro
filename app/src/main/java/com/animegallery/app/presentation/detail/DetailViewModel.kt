package com.animegallery.app.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegallery.app.domain.model.DownloadProgress
import com.animegallery.app.domain.model.ImageDetail
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.domain.usecase.CheckFavoriteStatusUseCase
import com.animegallery.app.domain.usecase.DownloadImageUseCase
import com.animegallery.app.domain.usecase.GetImageDetailUseCase
import com.animegallery.app.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the detail screen
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getImageDetailUseCase: GetImageDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val downloadImageUseCase: DownloadImageUseCase,
    private val checkFavoriteStatusUseCase: CheckFavoriteStatusUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val imageId: String = checkNotNull(savedStateHandle["imageId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadImageDetail()
        checkFavoriteStatus()
    }

    private fun loadImageDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getImageDetailUseCase(imageId)
                .onSuccess { imageDetail ->
                    _uiState.update {
                        it.copy(
                            image = imageDetail,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load image"
                        )
                    }
                }
        }
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            val isFavorite = checkFavoriteStatusUseCase(imageId)
            _uiState.update { it.copy(isFavorite = isFavorite) }
        }
    }

    fun onFavoriteToggled() {
        viewModelScope.launch {
            val currentImage = _uiState.value.image ?: return@launch
            
            // Convert ImageDetail to ImageItem for the use case
            val imageItem = ImageItem(
                id = currentImage.id,
                thumbnailUrl = currentImage.fileUrl,
                previewUrl = currentImage.fileUrl,
                fileUrl = currentImage.fileUrl,
                width = currentImage.width,
                height = currentImage.height,
                rating = currentImage.rating,
                tags = currentImage.tags
            )
            
            toggleFavoriteUseCase(imageItem)
                .onSuccess { isFavorite ->
                    _uiState.update { it.copy(isFavorite = isFavorite) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message ?: "Failed to toggle favorite")
                    }
                }
        }
    }

    fun onDownloadClicked() {
        viewModelScope.launch {
            val currentImage = _uiState.value.image ?: return@launch
            
            downloadImageUseCase(currentImage.fileUrl, currentImage.id)
                .collect { progress ->
                    when (progress) {
                        is DownloadProgress.Progress -> {
                            _uiState.update {
                                it.copy(
                                    downloadProgress = progress.percentage,
                                    isDownloading = true
                                )
                            }
                        }
                        is DownloadProgress.Success -> {
                            _uiState.update {
                                it.copy(
                                    downloadProgress = null,
                                    isDownloading = false,
                                    isDownloaded = true
                                )
                            }
                        }
                        is DownloadProgress.Error -> {
                            _uiState.update {
                                it.copy(
                                    downloadProgress = null,
                                    isDownloading = false,
                                    error = progress.message
                                )
                            }
                        }
                    }
                }
        }
    }

    fun onRetry() {
        loadImageDetail()
    }
}

/**
 * UI state for the detail screen
 */
data class DetailUiState(
    val image: ImageDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Float? = null
)
