package com.tiagohs.cinema_history.presentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setImageDrawableColored
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.domain.presenter.PresentationPresenter
import com.tiagohs.cinema_history.presentation.adapters.SumarioPresentationAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.entities.enums.ViewPosition
import com.tiagohs.domain.views.PresentationView
import kotlinx.android.synthetic.main.activity_presentation.*
import javax.inject.Inject


class PresentationActivity: BaseActivity(), PresentationView {

    private var mainTopic: MainTopicItem? = null

    override fun onGetLayoutViewId(): Int = R.layout.activity_presentation
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: PresentationPresenter

    var isFirstEnter = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar(toolbar)

        getApplicationComponent()?.inject(this)

        presenter.onBindView(this)
        presenter.fetchMoviesByListId(mainTopic)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        AnimationUtils.createScaleUpAnimation(startButton, 1f, 0f, 1f, 0f, 0.5f, 0.5f, 200)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()

        if (isFirstEnter) {
            AnimationUtils.createScaleUpAnimation(startButton, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200)
            isFirstEnter = false
        }
    }

    override fun setupArguments() {
        mainTopic = intent.getSerializableExtra(MAIN_TOPIC) as? MainTopicItem
    }

    override fun bindSumarioHeader() {
        mainTopic ?: return

        mainTopicTitle.text = mainTopic?.title
        mainTopicDescription.text = mainTopic?.description

        bindQuote(mainTopic)
        loadImage(mainTopic)
    }

    private fun bindQuote(mainTopic: MainTopicItem?) {
        val position = mainTopic?.quotePosition ?: ViewPosition.BOTTOM_END
        val quote = mainTopic?.quote ?: return

        quoteText.text = quote.quote
        quoteTextAuthor.text = quote.author

        bindQuotePosition(position)
        bindQuoteColor(quote)
    }

    private fun bindQuotePosition(position: ViewPosition) {

        val layoutParams = CollapsingToolbarLayout.LayoutParams(
            CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT,
            CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            when (position) {
                ViewPosition.BOTTOM_END -> {
                    gravity = Gravity.BOTTOM or Gravity.END
                    setMargins(16.convertIntToDp(this@PresentationActivity), 0, 16.convertIntToDp(this@PresentationActivity), 0)
                }
                ViewPosition.BOTTOM_START -> {
                    gravity = Gravity.BOTTOM or Gravity.START
                    setMargins(16.convertIntToDp(this@PresentationActivity), 0, 16.convertIntToDp(this@PresentationActivity), 0)
                }
                ViewPosition.TOP_END -> {
                    gravity = Gravity.TOP or Gravity.END
                    setMargins(16.convertIntToDp(this@PresentationActivity), 20.convertIntToDp(this@PresentationActivity), 16.convertIntToDp(this@PresentationActivity), 0)
                }
                ViewPosition.TOP_START -> {
                    gravity = Gravity.TOP or Gravity.START
                    setMargins(16.convertIntToDp(this@PresentationActivity), 100.convertIntToDp(this@PresentationActivity), 16.convertIntToDp(this@PresentationActivity), 0)
                }
            }
        }

        quoteContainer.layoutParams = layoutParams
    }

    private fun bindQuoteColor(quote: com.tiagohs.entities.Quote) {
        quote.textColor?.let {
            quoteText.setTextColor(getResourceColor(it))
            quoteTextAuthor.setTextColor(getResourceColor(it))
        }
        quote.backgroundColor?.let {
            quoteCard.setCardBackgroundColor(getResourceColor(it))
        }
        quote.iconColor?.let {
            quoteTop.setImageDrawableColored(R.drawable.ic_quote_bottom_24dp, it)
            quoteBottom.setImageDrawableColored(R.drawable.ic_quote_top_24dp, it)
        }
    }

    override fun bindMainTopicPresentation(mainTopic: MainTopicItem?) {
        this.mainTopic = mainTopic ?: return

        val adapter = SumarioPresentationAdapter(this, mainTopic.sumarioList)
        adapter.onSumarioClick = { _,  position ->
            startActivityWithSlideAnimation(HistoryPagesActivity.newIntent(this, mainTopic, position))
        }

        sumarioList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        sumarioList.adapter = adapter
    }

    private fun loadImage(mainTopic: MainTopicItem?) {
        val presentationImage = mainTopic?.presentationImage ?: return

        image.loadImage(presentationImage, null)
    }

    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"

        fun newInstance(context: Context, mainTopic: MainTopicItem): Intent {
            val intent = Intent(context, PresentationActivity::class.java)

            intent.putExtra(MAIN_TOPIC, mainTopic)

            return intent
        }
    }
}