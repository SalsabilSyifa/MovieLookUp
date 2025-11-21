package com.example.movielookup.api

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey val Id: Int,
    val title: String,
    val fullPoster: String,
    val vote_average: Double
)
