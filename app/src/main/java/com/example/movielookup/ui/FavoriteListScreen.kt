package com.example.movielookup.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.movielookup.api.AppDatabase
import com.example.movielookup.api.FavoriteMovie
import kotlinx.coroutines.launch

@Composable
fun FavoriteListScreen(onMovieClick: (Int) -> Unit) {

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    val favorites by db.favoriteDao()
        .getAllFavorites()
        .collectAsState(initial = emptyList())

    LazyColumn {
        items(favorites) { movie ->

            var isFavorite by remember { mutableStateOf(true) }
            val scope = rememberCoroutineScope()

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                AsyncImage(
                    model = movie.fullPoster,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )

                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .clickable { onMovieClick(movie.Id) }
                ) {
                    Text(movie.title)

                    // ⭐ TAMPILKAN RATING
                    Text(
                        text = "⭐ ${movie.vote_average}",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFFFFC107)
                    )
                }

                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            scope.launch {
                                db.favoriteDao().removeFavoriteById(movie.Id)
                                isFavorite = false
                            }
                        }
                )
            }
        }
    }
}
