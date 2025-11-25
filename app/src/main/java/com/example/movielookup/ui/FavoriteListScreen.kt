package com.example.movielookup.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movielookup.R
import com.example.movielookup.api.AppDatabase
import com.example.movielookup.api.FavoriteMovie
import com.example.movielookup.ui.theme.RatingGold
import kotlinx.coroutines.launch

@Composable
fun FavoriteListScreen(onMovieClick: (Int) -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val favorites by db.favoriteDao().getAllFavorites().collectAsState(initial = emptyList())
    val orientation = LocalConfiguration.current.orientation

    LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 8.dp)) {
        items(favorites) { movie ->
            FavoriteCard(
                movie = movie,
                onMovieClick = onMovieClick,
                db = db,
                isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
            )
        }
    }
}

@Composable
fun FavoriteCard(movie: FavoriteMovie, db: AppDatabase, onMovieClick: (Int) -> Unit, isLandscape: Boolean) {
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .fillMaxWidth()
            .clickable { onMovieClick(movie.Id) },
        shape = RoundedCornerShape(14.dp),
        elevation = 6.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = movie.fullPoster,
                contentDescription = null,
                modifier = Modifier
                    .width(if (isLandscape) 120.dp else 100.dp)
                    .height(if (isLandscape) 170.dp else 150.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = movie.title,
                    fontFamily = FontFamily(Font(R.font.poppinssemibold)),
                    fontSize = if (isLandscape) 20.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "⭐ ${movie.vote_average}",
                    fontSize = if (isLandscape) 18.sp else 15.sp,
                    color = RatingGold
                )
            }
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFE50914),
                modifier = Modifier
                    .size(if (isLandscape) 28.dp else 24.dp)
                    .align(Alignment.Top)
                    .clickable {
                        scope.launch { db.favoriteDao().removeFavoriteById(movie.Id) }
                    }
            )
        }
    }
}
