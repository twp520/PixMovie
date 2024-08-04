package com.flight.movie.infra.master.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.ActorItem
import com.flight.movie.infra.master.databinding.CommonListLayoutBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.base.CommonListViewHelper
import com.flight.movie.infra.master.ui.home.adapter.ActorListAdapter
import com.flight.movie.infra.master.ui.startPeopleDetail
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/2
 */
class HomeActorFragment : Fragment() {

    private val viewModel by viewModels<HomeActorViewModel>()
    private lateinit var actorListAdapter: ActorListAdapter
    private lateinit var binding: CommonListLayoutBinding
    private lateinit var viewHelper: CommonListViewHelper
    private var interLoader: InterLoader? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ShareHelper.userActivityClicked()
        binding = CommonListLayoutBinding.inflate(inflater, container, false)
        actorListAdapter = ActorListAdapter(viewModel.viewModelScope, viewModel.getFrom())
        viewHelper = CommonListViewHelper(requireContext(), binding, actorListAdapter, viewModel)
        viewModel.loadUiData()
        interLoader = InterLoader(
            getString(R.string.inter_test),
            lifecycleScope,
            AnalysisUtils.FROM_ENTER_DETAIL_PEOPLE_INTER
        )
        if (InstallManager.getRunB()) {
            interLoader?.load(requireContext())
        }
        actorListAdapter.setOnItemClickListener { _, _, position ->
            ShareHelper.userActivityClicked()
            val actorItem = actorListAdapter.getItem(position)
            if (actorItem is ActorItem) {
                interLoader?.show(requireActivity()) {
                    startPeopleDetail(requireContext(), actorItem.id)
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiStateItem.collectLatest {
                viewHelper.handleData(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interLoader?.destroy()
    }

}