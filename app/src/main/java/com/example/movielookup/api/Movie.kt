package com.example.movielookup.api

import com.google.gson.annotations.SerializedName


data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val backdrop_path: String?,   // ➕ Add
    val vote_average: Double,     // ➕ Rating
    val genre_ids: List<Int>,
    @SerializedName("poster_path")
    val posterPath: String?
) {
    val fullPoster: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"

    val fullBackdrop: String
        get() = "https://image.tmdb.org/t/p/w780$backdrop_path"
}
