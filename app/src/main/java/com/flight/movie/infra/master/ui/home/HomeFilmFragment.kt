package com.flight.movie.infra.master.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.databinding.CommonListLayoutBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.home.adapter.HomeCategoryAdapter
import com.flight.movie.infra.master.ui.startDetail
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/3
 */
abstract class HomeFilmFragment : Fragment() {

    abstract val filmType: String

    private val viewModel by viewModels<HomeCategoryViewModel>()
    private lateinit var categoryAdapter: HomeCategoryAdapter
    private lateinit var binding: CommonListLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ShareHelper.userActivityClicked()
        binding = CommonListLayoutBinding.inflate(inflater, container, false)
        binding.dataList.layoutManager = LinearLayoutManager(requireContext())
        categoryAdapter = HomeCategoryAdapter(filmType)
        binding.dataList.adapter = categoryAdapter
        categoryAdapter.addOnItemChildClickListener(R.id.item_home_category_show_more) { _, _, position ->
            categoryAdapter.getItem(position)?.let {
                //jump to list page
                ShareHelper.userActivityClicked()
                val intent = Intent(requireContext(), HomeMoreActivity::class.java)
                intent.putExtra("type", it.type)
                intent.putExtra("category", it.categoryString)
                intent.putExtra("display_category", it.title)
                startActivity(intent)
            }
        }

        categoryAdapter.onFilmItemClickListener = {
            ShareHelper.userActivityClicked()
            viewModel.showInterAD(requireActivity()) {
                startDetail(requireContext(), filmType, it)
            }
        }
        val from = if (filmType == DataClient.TYPE_MOVIE) AnalysisUtils.FROM_ENTER_DETAIL_MOVIE_INTER
        else AnalysisUtils.FROM_ENTER_DETAIL_TV_INTER
        viewModel.initInterAD(
            requireActivity(), getString(R.string.inter_test),
            from
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.requestCategory(requireContext(), filmType)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoryData.collect {
                if (it == null) {
                    binding.dataList.isVisible = false
                    binding.dataLoading.isVisible = true
                    binding.dataError.isVisible = false
                } else {
                    binding.dataLoading.isVisible = false
                    binding.dataList.isVisible = it.isSuccess
                    binding.dataError.isVisible = it.isFailure
                    if (it.isSuccess) {
                        AnalysisUtils.logEvent("home_film_show")
                        categoryAdapter.submitList(it.getOrNull())
                    }
                }
            }
        }

        lifecycleScope.launch {

            viewModel.nativeAdList.collectLatest {
                it ?: return@collectLatest
                categoryAdapter.getItem(it.first)?.let { state ->
                    categoryAdapter[it.first] = state.copy(nativeAd = it.second)
                }
            }
        }

        binding.errorButton.setOnClickListener {
            ShareHelper.userActivityClicked()
            viewModel.retryCategory(requireContext(), filmType)
        }
    }

}