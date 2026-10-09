package com.tiagohs.cinema_history.presentation.fragments

import com.tiagohs.cinema_history.presentation.configs.StatusBarScrim

import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.FragmentHistoryPageBinding
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import androidx.recyclerview.widget.ConcatAdapter
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.AdPlacement
import com.tiagohs.cinema_history.ads.NativeAdAdapter
import com.tiagohs.cinema_history.presentation.activities.*
import com.tiagohs.cinema_history.presentation.adapters.PageContentAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.domain.presenter.HistoryPagePresenter
import com.tiagohs.domain.views.HistoryPageView
import com.tiagohs.entities.Page
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.enums.ContentType
import com.tiagohs.entities.Sumario
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.tools.HidingScrollListener
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import javax.inject.Inject
import kotlin.math.abs


class HistoryPageFragment : BaseFragment<FragmentHistoryPageBinding>(), HistoryPageView,
    HidingScrollListener.HidingScrollCallback {

    @Inject
    lateinit var settingManager: SettingsManager

    private var pageContent: Page? = null
    private var sumario: Sumario? = null
    private var mainTopic: MainTopicItem? = null

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentHistoryPageBinding.inflate(inflater, container, false)
    override fun onErrorAction() {}

    @Inject
    lateinit var presenter: HistoryPagePresenter

    private var footerShowsFromAppBar = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getApplicationComponent()?.inject(this)

        StatusBarScrim.apply(view)

        (activity as? BaseActivity<*>)?.setupToolbar(binding.toolbar, displayHomeAsUpEnabled = false)

        setHasOptionsMenu(true)

        presenter.onBindView(this)
        presenter.fetchPageContent(mainTopic?.id, sumario?.id)
    }

    override fun showLoading() {
        binding.loadHeaderView.showShimmer(true)
        binding.loadContentView.showShimmer(true)
        binding.loadHeaderView.show()
        binding.loadContentView.show()
    }

    override fun hideLoading() {
        binding.loadHeaderView.hideShimmer()
        binding.loadContentView.hideShimmer()
        binding.loadHeaderView.hide()
        binding.loadContentView.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.onUnbindView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history_page, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_glossary -> {
                activity?.startActivityWithSlideRightToLeftAnimation(GlossaryActivity.newIntent(context))
                true
            }
            R.id.action_references -> {
                activity?.startActivityWithSlideRightToLeftAnimation(ReferenceActivity.newIntent(context))
                true
            }
            else -> false
        }
    }

    override fun bindPageContent(pageContent: Page) {
        this.pageContent = pageContent

        binding.pageContentList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            // Anúncio nativo no meio do capítulo, entre dois parágrafos de texto: longe do rodapé com os
            // botões de navegação (próximo/compartilhar) e de outros elementos clicáveis.
            val contentList = pageContent.contentList
            val adAfter = chapterAdPosition(contentList)
            val createAdapter = { items: List<Content> ->
                PageContentAdapter(items, mainTopic, settingManager.getMovieLanguage()).apply {
                    presentScreen = { presentScreen(it) }
                    onMovieClicked = { onMovieSelected(it) }
                    onPersonClicked = { onPersonClicked(it) }
                    onLinkClicked = { onLinkClicked(it) }
                }
            }
            adapter = if (adAfter == null) createAdapter(contentList) else ConcatAdapter(
                createAdapter(contentList.take(adAfter)),
                NativeAdAdapter(AdPlacement.CHAPTER, viewLifecycleOwner),
                createAdapter(contentList.drop(adAfter))
            )
            addItemDecoration(
                SpaceOffsetDecoration(
                    10.convertIntToDp(context),
                    SpaceOffsetDecoration.TOP
                )
            )
            addOnScrollListener(HidingScrollListener(this@HistoryPageFragment, pageContent.contentList.size - 1))
        }

        val tv = TypedValue()
        val activity = activity ?: return

        if (activity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight =
                TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            binding.pageContentList.addItemDecoration(
                SpaceOffsetDecoration(
                    actionBarHeight,
                    SpaceOffsetDecoration.BOTTOM
                )
            )
        }

        setupHeader()
    }

    /** Posição do anúncio: entre dois textos, o mais perto possível do meio. Capítulos curtos ficam sem anúncio. */
    private fun chapterAdPosition(contents: List<Content>): Int? {
        if (contents.size < 6) return null

        return (2..contents.size - 2)
            .filter { contents[it - 1].type == ContentType.TEXT && contents[it].type == ContentType.TEXT }
            .minByOrNull { kotlin.math.abs(it - contents.size / 2) }
    }

    override fun onScrollUp() {
        (activity as? HistoryPagesActivity)?.hideFooter()
    }

    override fun onScrollDown() {
        (activity as? HistoryPagesActivity)?.showFooter()
    }

    override fun onLastItemCompletelyVisible() {
        (activity as? HistoryPagesActivity)?.showFooter()
    }

    override fun setupHeader() {
        binding.mainTopicName.setResourceText(mainTopic?.title)
        mainTopic?.color?.let { binding.mainTopicName.setResourceBackgroundColor(it) }
        binding.pageTitle.setResourceText(sumario?.title)
        binding.pageDescription.setResourceText(sumario?.description)

        val image = sumario?.image ?: return

        image.imageStyle?.height?.let {
            binding.pageHeaderImage.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                it.convertIntToDp(context)
            ).apply {
                topToBottom = R.id.headerContainer
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            }
        }

        binding.appBar.addOnOffsetChangedListener(onOffsetChangedListener())

        startAlphaAnimation(binding.mainTopicName, 200, 200)
        startAlphaAnimation(binding.pageTitle, 200, 400)
        startAlphaAnimation(binding.pageDescription, 200, 600) {
            // a animação pode terminar depois que o ViewPager destruiu a view desta página
            bindingOrNull?.pageHeaderImage?.loadImage(image, placeholder = null)
        }
        startAlphaAnimation(binding.pageContentList, 200, 800)
    }

    private fun startAlphaAnimation(view: View?, delay: Long, duration: Long, withEndAction: (() -> Unit)? = null) {
        try {
            view
                ?.animate()
                ?.alpha(1f)
                ?.setDuration(duration)
                ?.setStartDelay(delay)
                ?.setInterpolator(AccelerateDecelerateInterpolator())
                ?.withEndAction {
                    withEndAction?.invoke()
                }
                ?.start()
        } catch(ex: Exception) {
            view?.alpha = 1f
        }
    }

    private fun onOffsetChangedListener(): AppBarLayout.OnOffsetChangedListener {
        return AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val activity = (activity as? HistoryPagesActivity)
            val rect = Rect()

            appBarLayout.getHitRect(rect)

            if (verticalOffset == 0) {
                if (rect.bottom <= ViewCompat.getMinimumHeight(appBarLayout)) {
                    activity?.hideFooter()

                    footerShowsFromAppBar = false
                } else {
                    activity?.showFooter()

                    footerShowsFromAppBar = true
                }
            } else if (abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                if (rect.bottom <= ViewCompat.getMinimumHeight(appBarLayout)) {
                    activity?.hideFooter()

                    footerShowsFromAppBar = false
                } else {
                    activity?.showFooter()

                    footerShowsFromAppBar = true
                }
            }
        }
    }

    private fun presentScreen(intent: Intent) {
        activity?.startActivityWithSlideRightToLeftAnimation(intent)
    }

    private fun onMovieSelected(movieId: Int) {
        val context = context ?: return

        activity?.startActivityWithSlideRightToLeftAnimation(MovieDetailsActivity.newIntent(context, movieId))
    }

    private fun onPersonClicked(personId: Int) {
        val context = context ?: return

        activity?.startActivityWithSlideRightToLeftAnimation(PersonDetailsActivity.newIntent(context, personId))
    }

    private fun onLinkClicked(url: String) {
        context?.openLink(url)
    }

    override fun setupArguments() {
        sumario = arguments?.getSerializable(SUMARIO) as? Sumario
        mainTopic = (activity as? HistoryPagesActivity)?.mainTopic
    }

    companion object {
        const val SUMARIO = "SUMARIO"

        fun newInstance(sumario: Sumario): HistoryPageFragment {
            val historyPageFragment = HistoryPageFragment()
            val bundle = Bundle()

            bundle.putSerializable(SUMARIO, sumario)
            historyPageFragment.arguments = bundle

            return historyPageFragment
        }
    }
}