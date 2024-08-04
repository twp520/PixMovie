package com.flight.movie.infra.master.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.QuickAdapterHelper
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.flight.movie.infra.master.R
import com.flight.movie.infra.master.data.DataClient
import com.flight.movie.infra.master.data.PeopleDetail
import com.flight.movie.infra.master.databinding.ActivityActorDetailBinding
import com.flight.movie.infra.master.money.AnalysisUtils
import com.flight.movie.infra.master.money.InstallManager
import com.flight.movie.infra.master.money.InterLoader
import com.flight.movie.infra.master.money.ShareHelper
import com.flight.movie.infra.master.ui.COL_COUNT
import com.flight.movie.infra.master.ui.EXTRA_ID
import com.flight.movie.infra.master.ui.base.GridItemDecoration
import com.flight.movie.infra.master.ui.base.QuickAdHeaderAdapter
import com.flight.movie.infra.master.ui.detail.vm.ActorDetailViewModel
import com.flight.movie.infra.master.ui.home.adapter.FilmAdapter
import com.flight.movie.infra.master.ui.startDetail
import com.lm.piccolo.Piccolo
import com.lm.piccolo.view.ConductorForAdapter
import kotlinx.coroutines.launch

class ActorDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActorDetailBinding
    private val viewModel by viewModels<ActorDetailViewModel>()
    private val filmAdapter = FilmAdapter(emptyList(), "")
    private val adapterHelper = QuickAdapterHelper.Builder(filmAdapter).build()
    private lateinit var adListAdapter: QuickAdHeaderAdapter
    private lateinit var filmLoading: ConductorForAdapter
    private lateinit var interBackLoader: InterLoader
    private lateinit var interEnterDetailLoader: InterLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ShareHelper.userActivityClicked()
        val id = intent.getStringExtra(EXTRA_ID) ?: return
        adListAdapter =
            QuickAdHeaderAdapter(viewModel.viewModelScope, AnalysisUtils.FROM_DETAIL_PEOPLE_NATIVE)
        viewModel.requestPeopleDetail(id)
        interBackLoader = InterLoader(
            getString(R.string.inter_test), lifecycleScope,
            AnalysisUtils.FROM_BACK_INTER
        )
        interEnterDetailLoader = InterLoader(
            getString(R.string.inter_test), lifecycleScope,
            AnalysisUtils.FROM_ENTER_DETAIL_PEOPLE_INTER
        )

        interBackLoader.load(this)
        interEnterDetailLoader.load(this)
        binding.appbarContent.toolbar.setNavigationOnClickListener {
            ShareHelper.userActivityClicked()
            onBackPressedDispatcher.onBackPressed()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                ShareHelper.userActivityClicked()
                interBackLoader.show(this@ActorDetailActivity) {
                    finish()
                }
            }
        })
        binding.detail.layoutManager = QuickGridLayoutManager(this, COL_COUNT)
        binding.detail.addItemDecoration(
            GridItemDecoration(
                resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin), COL_COUNT
            )
        )

        filmLoading = Piccolo.createForList(binding.detail)

        filmLoading.items(
            intArrayOf(
                R.layout.item_film_main,
                R.layout.item_film_main,
                R.layout.item_film_main,
                R.layout.item_film_main,
                R.layout.item_film_main,
                R.layout.item_film_main
            )
        ).visible(true).adapter(adapterHelper.adapter)
        lifecycleScope.launch {
            viewModel.peopleDetail.collect {
                bindPeople(it)
            }
        }

        filmAdapter.setOnItemClickListener { _, _, position ->
            ShareHelper.userActivityClicked()
            filmAdapter.getItem(position)?.let {
                if (it.mediaType != null) {
                    if (InstallManager.getRunB()) {
                        interEnterDetailLoader.show(this) {
                            startDetail(this, it.mediaType, it)
                        }
                    } else {
                        startDetail(this, it.mediaType, it)
                    }
                }
            }
        }
        adapterHelper.addBeforeAdapter(adListAdapter)
    }

    @SuppressLint("SetTextI18n")
    private fun bindPeople(result: Result<PeopleDetail>?) {
        if (result == null) {
            //loading
            filmLoading.play()
        } else {
            result.getOrNull()?.let {
                filmLoading.visible(false).play()
                binding.appbarContent.toolbarLayout.title = it.name
                binding.appbarContent.tag.text = "${it.department} ${it.birthday}"
                binding.appbarContent.overview.text = it.biography
                Glide.with(this)
                    .load(DataClient.getImageUrl(it.profile))
                    .placeholder(R.drawable.shape_placeholder)
                    .error(R.drawable.baseline_broken_image_24)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.appbarContent.poster)
                filmAdapter.submitList(it.getFilmList())
            }
        }
    }
}