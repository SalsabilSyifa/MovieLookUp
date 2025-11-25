import android.R.attr.fontFamily
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movielookup.api.MovieApi
import com.example.movielookup.api.MovieDetailResponse
import com.example.movielookup.ui.FavoriteListScreen
import com.example.movielookup.ui.MovieDetailScreen
import com.example.movielookup.ui.MovieListScreen
import com.google.accompanist.navigation.animation.*
import com.example.movielookup.R
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        backgroundColor = MaterialTheme.colors.background
    ) { padding ->

        AnimatedNavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                MovieListScreen(
                    onMovieClick = { movie ->
                        navController.navigate("detail/${movie.id}")
                    }
                )
            }

            composable("favorites") {
                FavoriteListScreen(
                    onMovieClick = { id ->
                        navController.navigate("detail/$id")
                    }
                )
            }

            composable("detail/{id}") { back ->

                val movieId = back.arguments?.getString("id")?.toInt() ?: 0
                val movie = remember { mutableStateOf<MovieDetailResponse?>(null) }

                LaunchedEffect(movieId) {
                    movie.value = MovieApi.create().getMovieDetail(
                        movieId = movieId,
                        apiKey = "70292e98aa75c36564677891a39355ac",
                        language = Locale.getDefault().language
                    )
                }

                MovieDetailScreen(
                    movie = movie.value,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
        BottomNavigationItem(
            selected = currentRoute == "home",
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text(stringResource(id = R.string.home))
            },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
        )

        BottomNavigationItem(
            selected = currentRoute == "favorites",
            onClick = { navController.navigate("favorites") },
            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
            label = { Text(stringResource(id = R.string.favorites)) },
            selectedContentColor = MaterialTheme.colors.primary,
            unselectedContentColor = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
        )
    }
}
