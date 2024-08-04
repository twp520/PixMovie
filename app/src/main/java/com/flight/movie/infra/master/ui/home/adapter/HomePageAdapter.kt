package com.flight.movie.infra.master.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flight.movie.infra.master.ui.home.HomeMovieFragment
import com.flight.movie.infra.master.ui.home.HomeActorFragment
import com.flight.movie.infra.master.ui.home.HomeTvFragment

/**
 * create by colin
 * 2024/7/2
 */
class HomePageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeMovieFragment()
            1 -> HomeTvFragment()
            2 -> HomeActorFragment()
            else -> throw IllegalArgumentException("Did not create fragment")
        }
    }
}