package com.flight.movie.infra.master.data

import android.util.Log
import androidx.room.Room
import com.flight.movie.infra.master.MyApp
import com.flight.movie.infra.master.data.db.AppDatabase
import com.flight.movie.infra.master.ui.F_KEY_API_KEY
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

/**
 * create by colin
 * 2024/7/2
 */
object DataClient {

    const val TYPE_MOVIE = "movie"
    const val TYPE_TV = "tv"

    const val CATEGORY_TRENDING = "trending"
    const val CATEGORY_NOW_PLAYING_MOVIE = "now_playing"
    const val CATEGORY_NOW_PLAYING_TV = "airing_today"
    const val CATEGORY_POPULAR = "popular"
    const val CATEGORY_TOP_RATED = "top_rated"

    private const val baseUrl = "https://api.themoviedb.org/3/"
    private const val imageBaseUrl = "https://image.tmdb.org/t/p/w500/"

    var defLanguage: String = "en-US"
    var defLanguageName: MutableStateFlow<String> = MutableStateFlow("United States")

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder()
                .addInterceptor {
                    val oldReq = it.request()
                    return@addInterceptor if (oldReq.method == "GET") {
                        val oldUrl = oldReq.url
                        val imageUrl = oldUrl.pathSegments.last() == "images"
                        val newUrl = oldUrl.newBuilder()
                            .scheme(oldUrl.scheme)
                            .host(oldUrl.host)
                            .addQueryParameter(
                                "api_key", Firebase.remoteConfig.getString(
                                    F_KEY_API_KEY
                                )
                            )
                            .apply {
                                if (!imageUrl) {
                                    addQueryParameter("language", defLanguage)
                                }
                            }
                            .build()
                        Log.d("http", "request: $newUrl, imageUrl=$imageUrl")
                        val newReq = oldReq.newBuilder()
                            .method(oldReq.method, oldReq.body)
                            .url(newUrl)
                            .build()
                        it.proceed(newReq)
                    } else {
                        it.proceed(oldReq)
                    }
                }
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: MovieService by lazy { retrofit.create() }

    fun getImageUrl(suffix: String?): String = imageBaseUrl + suffix

    val appDB = Room.databaseBuilder(
        MyApp.instance.applicationContext,
        AppDatabase::class.java, "movie-app"
    ).build()
}