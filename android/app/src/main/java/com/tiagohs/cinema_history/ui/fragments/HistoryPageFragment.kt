package com.tiagohs.cinema_history.ui.fragments

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.Page
import com.tiagohs.cinema_history.models.Sumario
import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.presenter.HistoryPagePresenter
import com.tiagohs.cinema_history.ui.activities.HistoryPagesActivity
import com.tiagohs.cinema_history.ui.adapters.PageContentAdapter
import com.tiagohs.cinema_history.ui.configs.BaseFragment
import com.tiagohs.cinema_history.ui.custom.SpaceOffsetDecoration
import com.tiagohs.cinema_history.ui.views.HistoryPageView
import kotlinx.android.synthetic.main.fragment_history_page.*
import javax.inject.Inject
import kotlin.math.abs


class HistoryPageFragment: BaseFragment(), HistoryPageView {

    private var pageContent: Page? = null
    private var sumario: Sumario? = null
    private var mainTopic: MainTopicItem? = null

    override fun getViewID(): Int = R.layout.fragment_history_page
    override fun onErrorAction() {}

    @Inject
    lateinit var presenter: HistoryPagePresenter

    var hasShowFooter = false
    private var isUserScrolling = false
    private var footerShowsFromAppBar = false
    private var isListGoingUp = true

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

        pageContentList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        pageContentList.adapter = PageContentAdapter(context, pageContent.contentList)
        pageContentList.addItemDecoration(SpaceOffsetDecoration(10.convertIntToDp(context), SpaceOffsetDecoration.TOP))
        pageContentList.addOnScrollListener(onScrollListener())

        val tv = TypedValue()
        val activity = activity ?: return

        if (activity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            val actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            pageContentList.addItemDecoration(SpaceOffsetDecoration(actionBarHeight, SpaceOffsetDecoration.BOTTOM))
        }

        setupHeader()
    }

    private fun onScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val activity = (activity as? HistoryPagesActivity)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if ((pastVisibleItems + visibleItemCount >= totalItemCount) && !hasShowFooter) {
                    activity?.showFooter()

                    hasShowFooter = true
                } else if (hasShowFooter) {
                    activity?.hideFooter()
                    hasShowFooter = false
                }

            }

        }
    }

    override fun setupHeader() {
        mainTopicName.text = mainTopic?.title
        pageTitle.text = sumario?.title
        pageDescription.text = sumario?.description

        val image = sumario?.image ?: return

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