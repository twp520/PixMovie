package com.flight.movie.infra.master.ui.personal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FavoriteMovie
import com.flight.movie.infra.master.databinding.FragmentPersonalBinding
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.EXTRA_EP
import com.flight.movie.infra.master.ui.base.GridItemDecoration
import com.flight.movie.infra.master.ui.detail.TvEpDetailActivity
import com.flight.movie.infra.master.ui.detail.params.TvEpDetailParams
import com.flight.movie.infra.master.ui.home.FavoriteViewModel
import com.flight.movie.infra.master.ui.startDetail
import com.flight.movie.infra.master.ui.toFilm
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val watchListAdapter = WatchListAdapter()
    private val favoriteViewModel by viewModels<FavoriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        binding.favList.dataList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.favList.dataList.addItemDecoration(
            GridItemDecoration(
                resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin),
                3
            )
        )
        binding.favList.dataList.adapter = watchListAdapter
        watchListAdapter.setOnItemClickListener { _, _, position ->
            //remove
            watchListAdapter.getItem(position)?.let {
                if (it is FavoriteMovie) {
                    onMovieClicked(it)
                } else if (it is TvEpDetailParams) {
                    onTvClicked(it)
                }
            }
        }
        binding.favList.emptyTips.text = getString(R.string.empty_common_favorite)
        binding.favList.emptyButton.isVisible = false

        binding.buttonSetting.setOnClickListener {
            ShareHelper.userActivityClicked()
            //to setting
            startActivity(Intent(requireContext(), SettingActivity::class.java))
        }

        return binding.root
    }

    private fun onTvClicked(tvEpDetailParams: TvEpDetailParams) {
        ShareHelper.userActivityClicked()
        if (watchListAdapter.isEditModel) {
            favoriteViewModel.unFavoriteTv(tvEpDetailParams)
        } else {
            //jump to detail
            startActivity(Intent(requireContext(), TvEpDetailActivity::class.java).apply {
                putExtra(
                    EXTRA_EP,
                    tvEpDetailParams
                )
            })
        }
    }

    private fun onMovieClicked(favoriteMovie: FavoriteMovie) {
        ShareHelper.userActivityClicked()
        if (watchListAdapter.isEditModel) {
            favoriteViewModel.unFavoriteFilm(favoriteMovie)
        } else {
            //jump to detail
            startDetail(requireContext(), DataClient.TYPE_MOVIE, toFilm(favoriteMovie))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonDelete.setOnClickListener {
            watchListAdapter.isEditModel = !watchListAdapter.isEditModel
            binding.buttonDelete.setImageResource(
                if (watchListAdapter.isEditModel) {
                    R.drawable.baseline_done_24
                } else {
                    R.drawable.baseline_delete_24
                }
            )
        }

        lifecycleScope.launch {
            favoriteViewModel.favoriteData.collectLatest {
                binding.favList.dataLoading.isVisible = false
                binding.favList.dataList.isVisible = it.isNotEmpty()
                binding.favList.dataEmpty.isVisible = it.isEmpty()
                watchListAdapter.submitList(it)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        ShareHelper.userActivityClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}