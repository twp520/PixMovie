package com.flight.movie.infra.master.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flight.movie.infra.master.ui.detail.params.TvEpDetailParams
import kotlinx.coroutines.flow.Flow

/**
 * create by colin
 * 2024/7/11
 */
@Dao
interface FavTvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(tvEpDetailParams: TvEpDetailParams)

    @Delete
    fun deleteFilm(tvEpDetailParams: TvEpDetailParams)

    @Query("select * from tvepdetailparams")
    fun queryFavFilm(): Flow<List<TvEpDetailParams>>

    @Query("select * from tvepdetailparams where tvId=:tvId and seNumber=:seNumber")
    fun isFavorite(tvId: String, seNumber: Int): Flow<TvEpDetailParams?>
}