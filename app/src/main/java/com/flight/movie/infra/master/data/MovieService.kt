package com.flight.movie.infra.master.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * create by colin
 * 2024/7/2
 */
interface MovieService {


    //discover page
    /**
     * genres 流派筛选的条件之一
     *
     * @param type movie or tv
     */
    @GET("genre/{type}/list")
    suspend fun getGenres(@Path("type") type: String): GenresResult

    /**
     * use discover api to filter movie or tv, the params contain :genres,region,year etc...
     * @param type movie or tv
     */
    @GET("discover/{type}")
    suspend fun getDiscoverList(
        @Path("type") type: String,
        @QueryMap params: Map<String, String>
    ): ApiPage<FilmItem>


    //home page, top 3 category, tv and movie both need
    /**
     * get trending data
     * @param type movie or tv
     */
    @GET("trending/{type}/day")
    suspend fun getTrending(@Path("type") type: String): ApiPage<FilmItem>

    @GET("trending/{type}/day")
    suspend fun getTrendingPage(
        @Path("type") type: String,
        @Query("Page") page: Int
    ): ApiPage<FilmItem>

    /**
     * get home page category data
     * @param type movie or tv or person
     * @param category popular / top_rated / now_playing（tv: airing_today）
     */
    @GET("{type}/{category}")
    suspend fun getListByCategory(
        @Path("type") type: String,
        @Path("category") category: String
    ): ApiPage<FilmItem>

    @GET("{type}/{category}")
    suspend fun getListByCategoryPage(
        @Path("type") type: String,
        @Path("category") category: String,
        @Query("page") page: Int
    ): ApiPage<FilmItem>


    @GET("person/popular")
    suspend fun getPeopleList(
        @Query("page") page: Int
    ): ApiPage<ActorItem>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") id: String,
        @Query("append_to_response") ar: String
    ): MovieDetail

    @GET("tv/{id}")
    suspend fun getTVSeriesDetail(
        @Path("id") id: String
    ): TVSeriesDetail

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getTvDetail(
        @Path("tv_id") tvId: String,
        @Path("season_number") seNumber: Int,
        @Query("append_to_response") append: String
    ): TvEpDetail

    @GET("tv/{tv_id}/season/{season_number}/images")
    suspend fun getTvSeriesImages(
        @Path("tv_id") tvId: String,
        @Path("season_number") seNumber: Int,
    ): FilmImageResult

    @GET("tv/{tv_id}/recommendations")
    suspend fun getTvRecommend(
        @Path("tv_id") tvId: String,
    ): ApiPage<FilmItem>

    @GET("{type}/{movie_id}/images")
    suspend fun getFilmImages(
        @Path("type") type: String,
        @Path("movie_id") id: String
    ): FilmImageResult

    @GET("person/{person_id}")
    suspend fun getPeopleDetail(
        @Path("person_id") id: String,
        @Query("append_to_response") append: String
    ): PeopleDetail
}