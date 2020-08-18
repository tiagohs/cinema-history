package com.tiagohs.cinema_history.presentation.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.activities.HistoryPagesActivity
import com.tiagohs.cinema_history.presentation.adapters.PageContentAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseFragment
import com.tiagohs.domain.presenter.HistoryPagePresenter
import com.tiagohs.domain.views.HistoryPageView
import com.tiagohs.entities.Page
import com.tiagohs.entities.Sumario
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.HidingScrollListener
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import kotlinx.android.synthetic.main.fragment_history_page.*
import javax.inject.Inject
import kotlin.math.abs


class HistoryPageFragment : BaseFragment(), HistoryPageView,
    HidingScrollListener.HidingScrollCallback {

    private var pageContent: Page? = null
    private var sumario: Sumario? = null
    private var mainTopic: MainTopicItem? = null

    override fun getViewID(): Int = R.layout.fragment_history_page
    override fun onErrorAction() {}

    @Inject
    lateinit var presenter: HistoryPagePresenter

    private var footerShowsFromAppBar = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchPageContent(mainTopic?.id, sumario?.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.onUnbindView()
    }

    override fun bindPageContent(pageContent: Page) {
        this.pageContent = pageContent

        pageContentList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = PageContentAdapter(pageContent.contentList).apply {
                presentScreen = { presentScreen(it) }
            }
            addItemDecoration(
                SpaceOffsetDecoration(
                    10.convertIntToDp(context),
                    SpaceOffsetDecoration.TOP
                )
            )
            addOnScrollListener(HidingScrollListener(this@HistoryPageFragment))
        }

        val tv = TypedValue()
        val activity = activity ?: return

        if (activity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight =
                TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            pageContentList.addItemDecoration(
                SpaceOffsetDecoration(
                    actionBarHeight,
                    SpaceOffsetDecoration.BOTTOM
                )
            )
        }

        setupHeader()
    }

    override fun onScrollUp() {
        (activity as? HistoryPagesActivity)?.hideFooter()
    }

    override fun onScrollDown() {
        (activity as? HistoryPagesActivity)?.showFooter()
    }

    override fun setupHeader() {
        mainTopicName.setResourceText(mainTopic?.title)
        pageTitle.setResourceText(sumario?.title)
        pageDescription.setResourceText(sumario?.description)

        val image = sumario?.image ?: return

        image.imageStyle?.height?.let {
            pageHeaderImage.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                it.convertIntToDp(context)
            ).apply {
                topToBottom = R.id.headerContainer
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID
            }
        }

        pageHeaderImage.loadImage(image)

        appBar.addOnOffsetChangedListener(onOffsetChangedListener())
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
        startActivity(intent)
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