package com.example.movielookup.api

data class TranslateRequest(
    val q: String,
    val source: String = "en",
    val target: String = "id",
    val format: String = "text"
)