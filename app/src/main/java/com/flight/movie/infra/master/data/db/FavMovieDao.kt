package com.flight.movie.infra.master.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flight.movie.infra.master.data.FavoriteMovie
import kotlinx.coroutines.flow.Flow

/**
 * create by colin
 * 2024/7/11
 */
@Dao
interface FavMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(favoriteMovie: FavoriteMovie)

    @Delete
    fun deleteFilm(favoriteMovie: FavoriteMovie)

    @Query("select * from favoritemovie")
    fun queryFavFilm(): Flow<List<FavoriteMovie>>

    @Query("select * from favoritemovie where id=:id")
    fun isFavorite(id: String): Flow<FavoriteMovie?>
}