package com.example.movielookup.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TranslateApi {

    @Headers("Content-Type: application/json")
    @POST("translate")
    suspend fun translate(@Body request: TranslateRequest): TranslateResponse

    companion object {
        fun create(): TranslateApi {
            return Retrofit.Builder()
                .baseUrl("https://libretranslate.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TranslateApi::class.java)
        }
    }
}
