package com.flight.movie.infra.master.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FavoriteMovie
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.data.TVSeriesDetail
import com.flight.movie.infra.master.ui.detail.ActorDetailActivity
import com.flight.movie.infra.master.ui.detail.MovieDetailActivity
import com.flight.movie.infra.master.ui.detail.TvSeriesActivity
import com.flight.movie.infra.master.ui.detail.params.TvEpDetailParams


/**
 * create by colin
 * 2024/7/8
 */

const val TAG = "MovieMaster"
const val EXTRA_FILM = "film"
const val EXTRA_EP = "ep"
const val EXTRA_ID = "id"
const val EXTRA_COLD_START = "cold_start"
const val F_KEY_API_KEY = "ApiKey"
const val COL_COUNT = 3

const val MULTI_TYPE_AD = 1
const val MULTI_TYPE_DATA = 2

fun startDetail(context: Context, mediaType: String, filmItem: FilmItem) {
    if (mediaType == DataClient.TYPE_MOVIE) {
        context.startActivity(Intent(context, MovieDetailActivity::class.java).apply {
            putExtra(EXTRA_FILM, filmItem)
        })
    } else if (mediaType == DataClient.TYPE_TV) {
        context.startActivity(Intent(context, TvSeriesActivity::class.java).apply {
            putExtra(EXTRA_FILM, filmItem)
        })
    }
}

fun startPeopleDetail(context: Context, id: String) {
    context.startActivity(Intent(context, ActorDetailActivity::class.java).apply {
        putExtra(EXTRA_ID, id)
    })
}

fun createFromTvSeriesDetail(
    detail: TVSeriesDetail,
    seNumber: Int,
    tvRate: Float
): TvEpDetailParams {
    return TvEpDetailParams(
        detail.id,
        seNumber,
        detail.name,
        detail.displayGenres,
        detail.displayCountry,
        detail.poster,
        tvRate
    )
}

fun toFilm(favoriteMovie: FavoriteMovie): FilmItem {
    return FilmItem(
        favoriteMovie.id,
        favoriteMovie.name,
        favoriteMovie.name,
        favoriteMovie.poster,
        favoriteMovie.mediaType,
        favoriteMovie.vote
    )
}

fun fromFilm(filmItem: FilmItem): FavoriteMovie {
    return FavoriteMovie(
        filmItem.id,
        filmItem.displayName,
        filmItem.poster ?: "null",
        DataClient.TYPE_MOVIE,
        vote = filmItem.vote
    )
}


fun shareApp(context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=com.flight.movie.infra.master"
        )
        putExtra(Intent.EXTRA_TITLE, context.getString(R.string.share_title))
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

fun jump2Browser(context: Context, url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun go2market(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(
            "https://play.google.com/store/apps/details?id=com.flight.movie.infra.master"
        )
        setPackage("com.android.vending")
    }
    context.startActivity(intent)
}


fun formatVote(vote: Float): String {
    return "%.1f".format(vote)
}