package com.example.movielookup.api

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(movie: FavoriteMovie)

    @Query("SELECT * FROM favorite_movies")
    fun getAllFavorites(): Flow<List<FavoriteMovie>>

    @Query("DELETE FROM favorite_movies WHERE id = :id")
    suspend fun removeFavoriteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean
}

