package com.example.movielookup.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface GoogleTranslateApi {

    @GET("translate_a/single?client=gtx&dt=t")
    suspend fun translate(
        @Query("sl") source: String,
        @Query("tl") target: String,
        @Query("q") text: String
    ): List<Any>

    companion object {
        fun create(): GoogleTranslateApi {
            return Retrofit.Builder()
                .baseUrl("https://translate.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleTranslateApi::class.java)
        }
    }
}
