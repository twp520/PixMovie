package com.flight.movie.infra.master.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flight.movie.infra.master.data.FavoriteMovie
import com.flight.movie.infra.master.ui.detail.params.TvEpDetailParams

/**
 * create by colin
 * 2024/7/11
 */
@Database(
    entities = [FavoriteMovie::class, TvEpDetailParams::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favFilmDao(): FavMovieDao

    abstract fun favTvDao(): FavTvDao
}