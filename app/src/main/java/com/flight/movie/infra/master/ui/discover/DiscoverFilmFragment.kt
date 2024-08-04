package com.flight.movie.infra.master.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.databinding.FragmentDiscoverFilmBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.base.CommonListViewHelper
import com.flight.movie.infra.master.ui.startDetail
import com.flight.movie.infra.master.ui.state.DiscoverUiState
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/4
 */
class DiscoverFilmFragment : Fragment() {

    private lateinit var binding: FragmentDiscoverFilmBinding
    private lateinit var filmAdapter: DiscoverFilmAdapter
    private lateinit var viewModel: DiscoverViewModel
    private lateinit var listViewHelper: CommonListViewHelper
    private var interLoader: InterLoader? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ShareHelper.userActivityClicked()
        binding = FragmentDiscoverFilmBinding.inflate(inflater, container, false)
        val mediaType = arguments?.getString("type") ?: throw IllegalArgumentException("Not Type!")
        viewModel = viewModels<DiscoverViewModel>(factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DiscoverViewModel(mediaType) as T
                }
            }
        }).value
        filmAdapter = DiscoverFilmAdapter(mediaType, viewModel.viewModelScope, viewModel.getFrom())
        listViewHelper = CommonListViewHelper(
            requireContext(), binding.discoverList,
            filmAdapter, viewModel
        )
        binding.discoverList.emptyTips.text = getString(R.string.empty_common_filter)

        viewModel.initFilter(requireContext(), mediaType)

        binding.discoverBtnGenres.setOnClickListener {
            ShareHelper.userActivityClicked()
            viewModel.filterButtonClicked(DiscoverUiState.FILTER_TYPE_GENRES)
        }
        binding.discoverBtnCountry.setOnClickListener {
            ShareHelper.userActivityClicked()
            viewModel.filterButtonClicked(DiscoverUiState.FILTER_TYPE_COUNTRY)
        }
        binding.discoverBtnYear.setOnClickListener {
            ShareHelper.userActivityClicked()
            viewModel.filterButtonClicked(DiscoverUiState.FILTER_TYPE_YEAR)
        }

        binding.filterOk.setOnClickListener {
            //request api
            ShareHelper.userActivityClicked()
            viewModel.filterOK()
            viewModel.refresh()
        }

        binding.discoverList.emptyButton.setOnClickListener {
            ShareHelper.userActivityClicked()
            viewModel.refresh()
        }

        binding.filterReset.setOnClickListener {
            ShareHelper.userActivityClicked()
            when (viewModel.discoverUiState.value.showFilterType) {
                DiscoverUiState.FILTER_TYPE_GENRES -> {
                    binding.genresChipGroup.clearCheck()
                    viewModel.checkedGenres.clear()
                }

                DiscoverUiState.FILTER_TYPE_COUNTRY -> {
                    binding.countryChipGroup.clearCheck()
                    viewModel.checkedCountries.clear()

                }

                DiscoverUiState.FILTER_TYPE_YEAR -> {
                    binding.yearChipGroup.clearCheck()
                    viewModel.checkedYears.clear()
                }
            }
        }

        interLoader = InterLoader(
            getString(R.string.inter_test),
            lifecycleScope,
            AnalysisUtils.FROM_ENTER_DETAIL_DISCOVER_INTER
        )
        if (InstallManager.getRunB()) {
            interLoader?.load(requireContext())
        }
        filmAdapter.setOnItemClickListener { _, _, position ->
            ShareHelper.userActivityClicked()
            val item = filmAdapter.getItem(position)
            if (item is FilmItem) {
                interLoader?.show(requireActivity()) {
                    startDetail(requireContext(), mediaType, item)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refresh()
        lifecycleScope.launch {
            viewModel.discoverUiState.collect {
                createChip(
                    binding.genresChipGroup,
                    it.genresData,
                    viewModel.checkedGenres
                ) { genres ->
                    genres.name
                }
                createChip(
                    binding.countryChipGroup,
                    it.countriesData,
                    viewModel.checkedCountries
                ) { country ->
                    country.name
                }
                createChip(binding.yearChipGroup, it.yearData, viewModel.checkedYears) { year ->
                    year
                }

                binding.genresChipGroup.isVisible =
                    it.showFilterType == DiscoverUiState.FILTER_TYPE_GENRES
                binding.countryChipGroup.isVisible =
                    it.showFilterType == DiscoverUiState.FILTER_TYPE_COUNTRY
                binding.yearChipGroup.isVisible =
                    it.showFilterType == DiscoverUiState.FILTER_TYPE_YEAR
                binding.filterLayout.isVisible =
                    it.showFilterType != DiscoverUiState.FILTER_TYPE_NONE
                setFilterButtonIcon(
                    binding.discoverBtnCountry,
                    it.showFilterType,
                    DiscoverUiState.FILTER_TYPE_COUNTRY
                )
                setFilterButtonIcon(
                    binding.discoverBtnGenres,
                    it.showFilterType,
                    DiscoverUiState.FILTER_TYPE_GENRES
                )
                setFilterButtonIcon(
                    binding.discoverBtnYear,
                    it.showFilterType,
                    DiscoverUiState.FILTER_TYPE_YEAR
                )
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateItem.collectLatest {
                listViewHelper.handleData(it)
            }
        }
    }

    private fun setFilterButtonIcon(
        button: TextView,
        showedFilterType: Int, selfType: Int
    ) {
        val icon = if (showedFilterType == selfType) {
            R.drawable.baseline_arrow_drop_up_24
        } else {
            R.drawable.baseline_arrow_drop_down_24
        }
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)
    }

    private fun <T> createChip(
        chipGroup: ChipGroup,
        data: List<T>,
        checkedList: MutableList<T>,
        binding: (item: T) -> String
    ) {
        if (chipGroup.childCount > 0)
            return
        data.forEach {
            val chip = layoutInflater.inflate(R.layout.item_filter_chip, chipGroup, false) as Chip
            chip.text = binding.invoke(it)
            chip.setOnCheckedChangeListener { _, isChecked ->
                ShareHelper.userActivityClicked()
                if (isChecked) {
                    checkedList.add(it)
                } else {
                    checkedList.remove(it)
                }
            }
            chipGroup.addView(chip)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interLoader?.destroy()
    }
}