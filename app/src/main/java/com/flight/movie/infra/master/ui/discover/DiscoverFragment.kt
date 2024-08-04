package com.flight.movie.infra.master.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.databinding.FragmentDiscoverBinding
import com.flight.movie.infra.master.money.ShareHelper
import com.google.android.material.tabs.TabLayoutMediator

class DiscoverFragment : Fragment() {

    private var _binding: FragmentDiscoverBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mediator: TabLayoutMediator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ShareHelper.userActivityClicked()

        _binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        val pageAdapter = DiscoverPageAdapter(requireActivity())
        binding.discoverVp.adapter = pageAdapter
        val titles = resources.getStringArray(R.array.home_titles)
        mediator = TabLayoutMediator(
            binding.discoverTab,
            binding.discoverVp
        ) { tab, position ->
            tab.setText(titles[position])
        }
        mediator.attach()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        ShareHelper.userActivityClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediator.detach()
        _binding = null
    }
}