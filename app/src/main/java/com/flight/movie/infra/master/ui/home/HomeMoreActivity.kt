package com.flight.movie.infra.master.ui.home

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.FilmItem
import com.flight.movie.infra.master.databinding.ActivityHomeMoreBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.base.CommonListViewHelper
import com.flight.movie.infra.master.ui.home.adapter.HomeMoreListAdapter
import com.flight.movie.infra.master.ui.startDetail
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * create by colin
 * 2024/7/7
 */
class HomeMoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val type = intent?.getStringExtra("type")
        val category = intent?.getStringExtra("category")
        val title = intent?.getStringExtra("display_category")
        if (type == null || category == null) {
            finish()
            return
        }
        val viewModel by viewModels<HomeMoreViewModel>(factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeMoreViewModel(type, category) as T
                }
            }
        })
        val from = if (type == DataClient.TYPE_MOVIE) AnalysisUtils.FROM_ENTER_DETAIL_MOVIE_INTER
        else AnalysisUtils.FROM_ENTER_DETAIL_TV_INTER
        val interLoader = InterLoader(getString(R.string.inter_test), lifecycleScope, from)
        interLoader.load(this)

        binding.homeMoreToolbar.title = title
        binding.homeMoreToolbar.setNavigationOnClickListener {
            ShareHelper.userActivityClicked()
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ShareHelper.userActivityClicked()
                interLoader.show(this@HomeMoreActivity) {
                    finish()
                }
            }
        })

        val homeMoreListAdapter = HomeMoreListAdapter(type, lifecycleScope, from)
        val listViewHelper =
            CommonListViewHelper(this, binding.homeMoreList, homeMoreListAdapter, viewModel)

        homeMoreListAdapter.setOnItemClickListener { _, _, position ->
            ShareHelper.userActivityClicked()
            val item = homeMoreListAdapter.getItem(position)
            if (item is FilmItem) {
                interLoader.show(this) {
                    startDetail(this, type, item)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateItem.collectLatest {
                listViewHelper.handleData(it)
            }
        }
        viewModel.loadUiData()
    }

}