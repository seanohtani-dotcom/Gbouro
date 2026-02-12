package com.animegallery.app.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit,
    onImageClick: (String) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Image Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onFavoriteToggled) {
                        Icon(
                            imageVector = if (uiState.isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Favorite",
                            tint = if (uiState.isFavorite) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                    IconButton(
                        onClick = viewModel::onDownloadClicked,
                        enabled = !uiState.isDownloading
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error ?: "An error occurred",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = viewModel::onRetry) {
                        Text("Retry")
                    }
                }
            }
            uiState.image != null -> {
                DetailContent(
                    uiState = uiState,
                    onImageClick = onImageClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    uiState: DetailUiState,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val image = uiState.image ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Image - clickable for fullscreen
        AsyncImage(
            model = image.fileUrl,
            contentDescription = "Image ${image.id}",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(image.width.toFloat() / image.height.toFloat())
                .clickable { onImageClick(image.fileUrl) },
            contentScale = ContentScale.Fit
        )

        // Download progress
        if (uiState.isDownloading && uiState.downloadProgress != null) {
            LinearProgressIndicator(
                progress = uiState.downloadProgress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Image info
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resolution: ${image.width} × ${image.height}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rating: ${image.rating.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (image.source != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Source: ${image.source}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tags:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tags
            image.tags.forEach { tag ->
                AssistChip(
                    onClick = { },
                    label = { Text(tag) },
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}
