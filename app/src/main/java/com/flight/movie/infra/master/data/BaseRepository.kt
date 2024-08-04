package com.flight.movie.infra.master.data


/**
 * create by colin
 * 2024/4/25
 */
open class BaseRepository {

    protected suspend fun <T> callApi(block: suspend () -> T): Result<T> {
        return Result.runCatching {
            block.invoke()
        }.also {
            if (it.isFailure) {
                it.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}