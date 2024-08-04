package com.flight.movie.infra.master.ui.base

import android.content.Context
import androidx.core.view.isVisible
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.chad.library.adapter.base.loadState.LoadState
import com.chad.library.adapter.base.loadState.trailing.TrailingLoadStateAdapter
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.databinding.CommonListLayoutBinding
import com.flight.movie.infra.master.ui.COL_COUNT
import com.flight.movie.infra.master.ui.state.MultipleItemState

/**
 * create by colin
 * 2024/7/23
 */
class CommonListViewHelper(
    private val context: Context,
    private val binding: CommonListLayoutBinding,
    private val dataAdapter: BaseAdListAdapter,
    private val viewModel: BaseAdListViewModel

) : TrailingLoadStateAdapter.OnTrailingListener {

    private val adapterHelper = QuickAdapterHelper.Builder(dataAdapter)
        .setTrailingLoadStateAdapter(this).build()

    init {
        binding.dataList.layoutManager = QuickGridLayoutManager(context, COL_COUNT)
        binding.dataList.addItemDecoration(
            GridItemDecoration(
                context.resources.getDimensionPixelSize(
                    R.dimen.activity_horizontal_margin
                ), COL_COUNT
            )
        )
        binding.dataList.adapter = adapterHelper.adapter

        binding.errorButton.setOnClickListener {
            onFailRetry()
        }
    }

    fun handleData(it: Result<List<MultipleItemState>>?) {
        if (it == null) {
            binding.dataList.isVisible = false
            binding.dataLoading.isVisible = true
            binding.dataError.isVisible = false
            dataAdapter.submitList(emptyList())
        } else {
            binding.dataLoading.isVisible = false
            if (viewModel.isRefresh) {
                binding.dataList.isVisible = it.isSuccess
                binding.dataError.isVisible = it.isFailure
            } else {
                binding.dataList.isVisible = true
                binding.dataError.isVisible = false
            }
            if (it.isSuccess) {
                it.getOrNull()?.let { result ->
                    dataAdapter.addAll(result)
                }
                adapterHelper.trailingLoadState =
                    LoadState.NotLoading(false)
            } else {
                adapterHelper.trailingLoadState =
                    LoadState.Error(
                        it.exceptionOrNull() ?: RuntimeException("UnKnow Error")
                    )
            }
            binding.dataEmpty.isVisible = dataAdapter.itemCount == 0
            binding.dataList.isVisible = dataAdapter.itemCount > 0
        }
    }

    override fun onFailRetry() {
        viewModel.loadUiData()
    }

    override fun onLoad() {
        viewModel.loadUiData()
    }
}