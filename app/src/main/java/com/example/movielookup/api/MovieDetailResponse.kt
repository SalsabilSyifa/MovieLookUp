package com.example.movielookup.api

data class MovieDetailResponse(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Double,
    val genres: List<Genre>
)
data class Genre( val id: Int, val name: String )