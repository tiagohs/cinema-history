package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.graphics.drawable.DrawableCompat
import androidx.viewpager.widget.ViewPager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import com.tiagohs.cinema_history.models.tmdb.Movie
import com.tiagohs.cinema_history.presenter.MilMoviesPresentationPresenter
import com.tiagohs.cinema_history.ui.adapters.MoviesPagerAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.MilMoviesPresentationView
import kotlinx.android.synthetic.main.activity_mil_movies_presentation.*
import javax.inject.Inject


class MilMoviesPresentationActivity: BaseActivity(), MilMoviesPresentationView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_mil_movies_presentation
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: MilMoviesPresentationPresenter

    lateinit var mainTopic: MilMoviesMainTopic
    var moviesPagerAdapter: MoviesPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getApplicationComponent()?.inject(this)
        presenter.onBindView(this)

        setupArguments()
        presenter.fetchMoviesByListId(mainTopic.list_id)

        val titleColor = resources.getIdentifier(mainTopic.titleColor, "color", packageName)
        toolbar.getNavigationIcon()?.setColorFilter(resources.getColor(titleColor), PorterDuff.Mode.SRC_ATOP)

        presentationTitle.text = mainTopic.title

        presentationTitle.setTextColor(resources.getColor(titleColor))
        presentationSubtitle.setTextColor(resources.getColor(titleColor))
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onUnbindView()
    }

    override fun setupArguments() {
        mainTopic = intent?.extras?.getSerializable(MAIN_TOPIC) as? MilMoviesMainTopic ?: return
    }

    override fun bindMovieList(list: List<Movie>) {
        moviesPagerAdapter = MoviesPagerAdapter(supportFragmentManager, mainTopic, list)

        moviesViewPager.adapter = moviesPagerAdapter
        moviesViewPager.addOnPageChangeListener(onPageChangeListener())
    }

    private fun onPageChangeListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                moviesPagerAdapter?.onPageSelected(position)
            }

        }
    }

    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"

        fun newIntent(mainTopic: MilMoviesMainTopic, context: Context) : Intent {
            val intent = Intent(context, MilMoviesPresentationActivity::class.java)

            intent.putExtra(MAIN_TOPIC, mainTopic)

            return intent
        }
    }
}