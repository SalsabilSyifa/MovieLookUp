package com.example.movielookup.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movielookup.api.*
import kotlinx.coroutines.launch

@Composable
fun MovieListScreen(onMovieClick: (Movie) -> Unit) {

    val api = remember { MovieApi.create() }
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var page by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var endReached by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // 🔥 Load HALAMAN PERTAMA saat screen dibuka
    LaunchedEffect(Unit) {
        val response = api.getPopularMovies(
            apiKey = "70292e98aa75c36564677891a39355ac",
            page = 1
        )

        movies = response.results
        page = 2 // Next page
    }

    MovieListUI(
        movies = movies,
        db = db,
        loadNextPage = {
            if (isLoading || endReached) return@MovieListUI

            scope.launch {
                isLoading = true

                val response = api.getPopularMovies(
                    apiKey = "70292e98aa75c36564677891a39355ac",
                    page = page
                )

                // Jika halaman berikutnya kosong → stop
                if (response.results.isEmpty()) {
                    endReached = true
                } else {
                    movies = movies + response.results
                    page++
                }

                isLoading = false
            }
        },
        onMovieClick = onMovieClick
    )
}

@Composable
fun MovieListUI(
    movies: List<Movie>,
    db: AppDatabase,
    loadNextPage: () -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val scrollState = rememberLazyListState()

    val filtered = movies.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    // 🔥 AUTO LOAD PAGINATION SAAT SCROLL DEKAT BAGIAN BAWAH
    LaunchedEffect(scrollState.firstVisibleItemIndex, scrollState.layoutInfo.totalItemsCount) {
        val lastVisible = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

        if (lastVisible >= filtered.size - 4) {
            loadNextPage()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔍 SEARCH BOX
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search movies...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )

        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ) {

            items(filtered) { movie ->

                var isFavorite by remember { mutableStateOf(false) }

                // Cek favorit hanya saat movie.id berubah
                LaunchedEffect(movie.id) {
                    isFavorite = db.favoriteDao().isFavorite(movie.id)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMovieClick(movie) }
                        .padding(12.dp)
                ) {

                    AsyncImage(
                        model = movie.fullPoster,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                    ) {
                        Text(movie.title)
                        Text(
                            text = "⭐ ${movie.vote_average}",
                            style = MaterialTheme.typography.body2,
                            color = Color(0xFFFFC107) // warna gold
                        )

                        Text(movie.overview.take(100) + "…")
                    }

                    val scope = rememberCoroutineScope()

                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                scope.launch {
                                    val fav = FavoriteMovie(movie.id, movie.title, movie.fullPoster, movie.vote_average)

                                    if (isFavorite) db.favoriteDao().removeFavoriteById(movie.id)
                                    else db.favoriteDao().addFavorite(fav)

                                    isFavorite = !isFavorite
                                }
                            }
                    )
                }
            }
        }
    }
}
