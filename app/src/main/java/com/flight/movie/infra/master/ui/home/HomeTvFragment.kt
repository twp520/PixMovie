package com.flight.movie.infra.master.ui.home

import com.flight.movie.infra.master.data.DataClient

/**
 * create by colin
 * 2024/7/2
 */
class HomeTvFragment : HomeFilmFragment() {
    override val filmType: String
        get() = DataClient.TYPE_TV
}