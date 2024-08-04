package com.flight.movie.infra.master.data


/**
 * create by colin
 * 2024/7/2
 */
class MovieRepository(
    private val movieService: MovieService
) : BaseRepository() {

    /**
     * 请求首页trending分类的数据,仅仅请求单页数据且只需要前6个
     * @param type  type_movie、type_tv
     */
    suspend fun requestTrendingCategory(type: String): Result<ApiPage<FilmItem>> {
        return callApi {
            val originResult = movieService.getTrending(type)
            return@callApi if (originResult.results.size > 6) {
                val contentList = originResult.results.subList(0, 6)
                originResult.copy(results = contentList)
            } else {
                originResult
            }
        }
    }

    /**
     * 获取home页面category的数据，只需要6个
     */
    suspend fun requestHomeCategoryList(
        type: String,
        category: String
    ): Result<ApiPage<FilmItem>> {
        return callApi {
            val originResult = movieService.getListByCategory(type, category)
            return@callApi if (originResult.results.size > 6) {
                val contentList = originResult.results.subList(0, 6)
                originResult.copy(results = contentList)
            } else {
                originResult
            }
        }
    }

    suspend fun requestGenresList(type: String): Result<List<Genres>> {
        //todo use room cache
        return callApi {
            val result = movieService.getGenres(type)
            return@callApi result.genres
        }
    }

    suspend fun requestMovieDetail(movieId: String): Result<MovieDetail> {
        //需要推荐，演员，剧照
        val responseAppend = "credits,recommendations"
        return callApi {
            movieService.getMovieDetail(movieId, responseAppend)
        }
    }

    //TV剧季信息
    suspend fun requestTvSeriesDetail(tvId: String): Result<TVSeriesDetail> {
        return callApi {
            movieService.getTVSeriesDetail(tvId)
        }
    }

    //TV详情页面的数据，3个接口
    suspend fun requestTvEpDetail(tvId: String, seNumber: Int): Result<TvEpDetail> {
        return callApi {
            movieService.getTvDetail(tvId, seNumber, "credits")
        }
    }

    suspend fun requestTvRecommend(tvId: String): Result<ApiPage<FilmItem>> {
        return callApi {
            movieService.getTvRecommend(tvId)
        }
    }

    suspend fun requestTvImages(tvId: String, seNumber: Int): Result<FilmImageResult> {
        return callApi {
            movieService.getTvSeriesImages(tvId, seNumber)
        }
    }


    suspend fun requestFilmImages(type: String, id: String): Result<FilmImageResult> {
        return callApi {
            movieService.getFilmImages(type, id)
        }
    }

    suspend fun requestPeopleDetail(id:String):Result<PeopleDetail>{
        return callApi {
            movieService.getPeopleDetail(id,"movie_credits,tv_credits")
        }
    }
}