package com.example.movielookup.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movielookup.R
import com.example.movielookup.api.*
import com.example.movielookup.api.AppDatabase
import com.example.movielookup.api.FavoriteMovie
import com.example.movielookup.ui.theme.RatingGold
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

    LaunchedEffect(Unit) {
        val response = api.getPopularMovies(
            apiKey = "70292e98aa75c36564677891a39355ac",
            page = 1
        )
        movies = response.results
        page = 2
    }

    MovieListUI(
        movies = movies,
        db = db,
        onMovieClick = onMovieClick,
        loadNextPage = {
            if (isLoading || endReached) return@MovieListUI

            scope.launch {
                isLoading = true
                val response = api.getPopularMovies(
                    apiKey = "70292e98aa75c36564677891a39355ac",
                    page = page
                )
                if (response.results.isEmpty()) endReached = true
                else {
                    movies = movies + response.results
                    page++
                }
                isLoading = false
            }
        }
    )
}

@Composable
fun MovieListUI(
    movies: List<Movie>,
    db: AppDatabase,
    onMovieClick: (Movie) -> Unit,
    loadNextPage: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    val filtered = movies.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(scrollState.firstVisibleItemIndex, scrollState.layoutInfo.totalItemsCount) {
        val lastVisible = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        if (lastVisible >= filtered.size - 4) loadNextPage()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // 💜 Search Field pakai tema & Poppins
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search_movies),
                    fontFamily = FontFamily(Font(R.font.poppinsregular))
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily(Font(R.font.poppinsmedium))
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(18.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface,
                textColor = MaterialTheme.colors.onSurface,
                placeholderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colors.primary
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

    LaunchedEffect(movie.id) {
        isFavorite = db.favoriteDao().isFavorite(movie.id)

        try {
            val api = GoogleTranslateApi.create()
            val response = api.translate("en", "id", movie.overview)
            translated = (response[0] as List<*>)
                .joinToString("") { (it as List<*>)[0].toString() }
        } catch (e: Exception) { translated = movie.overview }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .clickable { onMovieClick(movie) },
        shape = RoundedCornerShape(14.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 6.dp
    ) {
        Row(modifier = Modifier.padding(12.dp)) {

            AsyncImage(
                model = movie.fullPoster,
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {

                // 🎬 Title
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.h6.copy(
                        fontFamily = FontFamily(Font(R.font.poppinssemibold)),
                        color = MaterialTheme.colors.onSurface
                    )
                )

                // ⭐ Rating
                Text(
                    text = "★ ${movie.vote_average}",
                    color = RatingGold,
                    style = MaterialTheme.typography.subtitle2.copy(
                        fontFamily = FontFamily(Font(R.font.poppinsmedium)),
                    ),
                    modifier = Modifier.padding(top = 3.dp)
                )

                // 📜 Overview
                Text(
                    text = (translated ?: movie.overview).take(100) + "…",
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.70f),
                    textAlign = TextAlign.Start,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(R.font.poppinsregular)),
                    modifier = Modifier.padding(top = 6.dp),
                    lineHeight = 18.sp
                )
            }
            val Red = Color(0xFFE50914)
            // 💗 Favorite Icon Adaptif Tema
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFavorite) Red else MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
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
