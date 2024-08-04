package com.flight.movie.infra.master.ui.discover

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flight.movie.infra.master.data.DataClient

/**
 * create by colin
 * 2024/7/2
 */
class DiscoverPageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DiscoverFilmFragment().apply {
                arguments = bundleOf(Pair("type", DataClient.TYPE_MOVIE))
            }

            1 -> DiscoverFilmFragment().apply {
                arguments = bundleOf(Pair("type", DataClient.TYPE_TV))
            }

            else -> throw IllegalArgumentException("Did not create fragment")
        }
    }
}