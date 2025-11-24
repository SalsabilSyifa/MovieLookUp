package com.example.movielookup.ui

import android.content.Context
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movielookup.api.*
import kotlinx.coroutines.launch
import java.util.Locale
import com.example.movielookup.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

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

    // Auto paginate
    LaunchedEffect(scrollState.firstVisibleItemIndex, scrollState.layoutInfo.totalItemsCount) {
        val lastVisible = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        if (lastVisible >= filtered.size - 4) loadNextPage()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // 🔍 Search Box Modern
        androidx.compose.material.TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(text = stringResource(id = R.string.search_movies)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xFF222222),
                textColor = Color.White,
                placeholderColor = Color.Gray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        LazyColumn(state = scrollState, modifier = Modifier.fillMaxSize()) {
            items(filtered) { movie -> MovieItemCard(movie, db, onMovieClick) }
        }
    }
}

@Composable
fun MovieItemCard(
    movie: Movie,
    db: AppDatabase,
    onMovieClick: (Movie) -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    var translated by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Cek favorit & Translate async
    LaunchedEffect(movie.id) {
        isFavorite = db.favoriteDao().isFavorite(movie.id)

        try {
            val api = GoogleTranslateApi.create()
            val response = api.translate("en", "id", movie.overview)
            translated = (response[0] as List<*>)
                .joinToString("") { (it as List<*>)[0].toString() }
        } catch (e: Exception) { translated = movie.overview }
    }

    androidx.compose.material.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { onMovieClick(movie) },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        elevation = 6.dp
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            AsyncImage(
                model = movie.fullPoster,
                contentDescription = null,
                modifier = Modifier
                    .size(110.dp)
                    .padding(4.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            )

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {

                // Judul
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.h6,
                    color = Color.White
                )

                // Rating badge
                Text(
                    text = "★ ${movie.vote_average}",
                    color = Color(0xFFFFD700),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(top = 3.dp)
                )

                // Overview
                Text(
                    text = (translated ?: movie.overview).take(100) + "…",
                    textAlign = TextAlign.Start,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 6.dp),
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }

            // Favorite icon
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavorite) Color.Red else Color.LightGray,
                modifier = Modifier
                    .size(26.dp)
                    .padding(start = 6.dp, top = 4.dp)
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
