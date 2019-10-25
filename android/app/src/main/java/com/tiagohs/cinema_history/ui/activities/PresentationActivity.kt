package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.Visibility
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.presenter.PresentationPresenter
import com.tiagohs.cinema_history.ui.adapters.SumarioPresentationAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.PresentationView
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
        supportFinishAfterTransition()
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

        quoteText.text = mainTopic?.quote?.quote
        quoteTextAuthor.text = mainTopic?.quote?.author

        loadImage(mainTopic)
    }

    override fun bindMainTopicPresentation(mainTopic: MainTopicItem?) {
        this.mainTopic = mainTopic ?: return

        val adapter = SumarioPresentationAdapter(this, mainTopic.sumarioList)
        adapter.onSumarioClick = { sumario,  position ->
            startActivity(HistoryPagesActivity.newIntent(this, mainTopic, position))

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        sumarioList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        sumarioList.adapter = adapter
    }

    private fun loadImage(mainTopic: MainTopicItem?) {
        val presentationImage = mainTopic?.image ?: return

        presentationImage.imageStyle?.height = 350

        image.loadImage(presentationImage)
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