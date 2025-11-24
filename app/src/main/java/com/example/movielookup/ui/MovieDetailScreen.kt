package com.example.movielookup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.example.movielookup.R
import com.example.movielookup.api.GoogleTranslateApi
import com.example.movielookup.api.MovieDetailResponse

@Composable
fun MovieDetailScreen(movie: MovieDetailResponse?, onBack: () -> Unit) {

    if (movie == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val colors = MaterialTheme.colors

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
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        // GRADIENT OVERLAY
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            colors.background.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // SCROLLABLE CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(Modifier.padding(16.dp)) {

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
                        color = colors.onBackground
                    )
                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.rating) + ": ⭐ ${movie.vote_average}",
                        color = colors.primary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.padding(horizontal = 16.dp)) {
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
                            color = colors.onBackground
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.overview),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(12.dp),
            )
            var translated by remember { mutableStateOf<String?>(null) }
            var isTranslating by remember { mutableStateOf(true) }


            LaunchedEffect(movie.overview) {
                try {
                    val api = GoogleTranslateApi.create()
                    val response = api.translate(
                        source = "en",
                        target = "id",
                        text = movie.overview
                    )

                    val translatedText = (response[0] as List<*>)
                        .joinToString("") { (it as List<*>)[0].toString() }

                    translated = translatedText

                } catch (e: Exception) {
                    e.printStackTrace()
                    translated = movie.overview
                } finally {
                    isTranslating = false   // <=== WAJIB ADA!
                }
            }

            if (isTranslating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Text(
                    text = translated ?: movie.overview,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Justify
                )
            }


            Spacer(Modifier.height(40.dp))
        }

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
