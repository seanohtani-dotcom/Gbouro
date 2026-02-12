package com.animegallery.app.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.animegallery.app.R
import com.animegallery.app.domain.model.ImageItem
import com.animegallery.app.presentation.auth.AuthViewModel
import com.animegallery.app.presentation.components.ShimmerLoadingGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onImageClick: (String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val images = viewModel.images.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anime Gallery") },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites"
                        )
                    }
                    IconButton(
                        onClick = {
                            if (authState.isAuthenticated) {
                                onNavigateToProfile()
                            } else {
                                onNavigateToLogin()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = if (authState.isAuthenticated) "Profile" else "Sign In"
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
        ) {
            // Search bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onSearch = viewModel::onSearchSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Tag suggestions
            if (uiState.tagSuggestions.isNotEmpty()) {
                TagSuggestions(
                    suggestions = uiState.tagSuggestions,
                    onTagClick = viewModel::onTagSelected,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Image grid
            when {
                images.loadState.refresh is LoadState.Loading && images.itemCount == 0 -> {
                    ShimmerLoadingGrid(modifier = Modifier.fillMaxSize())
                }
                images.loadState.refresh is LoadState.Error -> {
                    ErrorView(
                        message = (images.loadState.refresh as LoadState.Error).error.message
                            ?: "An error occurred",
                        onRetry = { images.retry() }
                    )
                }
                images.itemCount == 0 -> {
                    EmptyStateView()
                }
                else -> {
                    ImageGrid(
                        images = images,
                        onImageClick = onImageClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search tags...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onSearch() }
        )
    )
}

@Composable
private fun ImageGrid(
    images: LazyPagingItems<ImageItem>,
    onImageClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(images.itemCount) { index ->
            images[index]?.let { image ->
                ImageGridItem(
                    image = image,
                    onClick = { onImageClick(image.id) }
                )
            }
        }

        // Loading indicator at bottom
        if (images.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ImageGridItem(
    image: ImageItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = image.thumbnailUrl,
            contentDescription = "Image ${image.id}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyStateView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No images found",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TagSuggestions(
    suggestions: List<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        suggestions.take(5).forEach { tag ->
            SuggestionChip(
                onClick = { onTagClick(tag) },
                label = { Text(tag) }
            )
        }
    }
}
