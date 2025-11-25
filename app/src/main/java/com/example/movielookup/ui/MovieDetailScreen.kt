package com.example.movielookup.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.movielookup.R
import com.example.movielookup.api.GoogleTranslateApi
import com.example.movielookup.api.MovieDetailResponse
import com.example.movielookup.ui.theme.RatingGold

@Composable
fun MovieDetailScreen(movie: MovieDetailResponse?, onBack: () -> Unit) {

    if (movie == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val colors = MaterialTheme.colors
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {

        // BACKDROP
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w780${movie.backdrop_path}",
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLandscape) 200.dp else 300.dp),
            contentScale = ContentScale.Crop
        )

        // GRADIENT OVERLAY
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLandscape) 200.dp else 300.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, colors.background.copy(alpha = 0.95f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (isLandscape) 100.dp else 220.dp)
                .verticalScroll(rememberScrollState())
        ) {

            if (isLandscape) {
                // LANDSCAPE MODE (2 columns)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        // keep default modifier (no extra horizontal padding here)
                        MovieDetailHeader(movie = movie, colors = colors)
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1.2f),
                    ) {
                        MovieSynopsis(movie = movie, colors = colors)
                    }
                }

            } else {
                // PORTRAIT MODE -- add horizontal padding so poster isn't stuck to the edge
                MovieDetailHeader(
                    movie = movie,
                    colors = colors,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                MovieSynopsis(movie = movie, colors = colors)
            }

            Spacer(Modifier.height(40.dp))
        }

        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(42.dp)
                .background(colors.surface.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colors.onSurface
            )
        }
    }
}


@Composable
private fun MovieDetailHeader(
    movie: MovieDetailResponse,
    colors: Colors,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {

        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            modifier = Modifier.size(width = 120.dp, height = 180.dp)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(Modifier.align(Alignment.CenterVertically)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.h5.copy(fontSize = 26.sp),
                color = colors.onBackground,
                fontFamily = FontFamily(Font(R.font.poppinssemibold))
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.rating) + ": ⭐ ${movie.vote_average}",
                color = RatingGold,
                fontFamily = FontFamily(Font(R.font.poppinsregular)),
                fontSize = 18.sp
            )
        }
    }

    Spacer(Modifier.height(8.dp))

    Row(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        movie.genres.take(3).forEach { genre ->
            Box(
                modifier = Modifier
                    .background(
                        colors.primary.copy(alpha = 0.15f),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = genre.name,
                    color = colors.onBackground,
                    fontFamily = FontFamily(Font(R.font.poppinsregular))
                    )
            }
            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun MovieSynopsis(movie: MovieDetailResponse, colors: Colors) {
    Spacer(Modifier.height(16.dp))

    Text(
        text = stringResource(R.string.overview),
        style = MaterialTheme.typography.h6,
        fontFamily = FontFamily(Font(R.font.poppinssemibold)),
        modifier = Modifier.padding(12.dp),
        color = colors.onBackground
    )

    var translated by remember { mutableStateOf<String?>(null) }
    var isTranslating by remember { mutableStateOf(true) }

    LaunchedEffect(movie.overview) {
        try {
            val api = GoogleTranslateApi.create()
            val response = api.translate("en", "id", movie.overview)
            val translatedText = (response[0] as List<*>)
                .joinToString("") { (it as List<*>)[0].toString() }
            translated = translatedText
        } catch (e: Exception) {
            translated = movie.overview
        } finally {
            isTranslating = false
        }
    }

    if (isTranslating) {
        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Text(
            text = translated ?: movie.overview,
            fontFamily = FontFamily(Font(R.font.poppinsregular)),
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Justify,
            color = colors.onBackground
        )
    }
}
