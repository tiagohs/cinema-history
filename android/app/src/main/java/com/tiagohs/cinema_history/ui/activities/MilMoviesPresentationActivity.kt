package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.presenter.MilMoviesPresentationPresenter
import com.tiagohs.cinema_history.ui.adapters.MovieListAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.helpers.tools.ScaleMovieImageTransformer
import com.tiagohs.cinema_history.ui.views.MilMoviesPresentationView
import kotlinx.android.synthetic.main.activity_mil_movies_presentation.*
import javax.inject.Inject
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp


class MilMoviesPresentationActivity: BaseActivity(), MilMoviesPresentationView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_mil_movies_presentation
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: MilMoviesPresentationPresenter

    lateinit var mainTopic: MilMoviesMainTopic

    var page = 1
    var isSearching = false

    var adapter: MovieListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)
        presenter.onBindView(this)

        setupToolbar(toolbar)
        setupArguments()
        presenter.fetchMoviesByListId(mainTopic.list_id)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onUnbindView()
    }

    override fun setupArguments() {
        mainTopic = intent?.extras?.getSerializable(MAIN_TOPIC) as? MilMoviesMainTopic ?: return
    }

    override fun bindMovieList(list: List<Movie>) {
        adapter = MovieListAdapter(this, ArrayList(list), mainTopic)

        adapter?.onMovieSelected = { movie, position -> onMovieSelected(movie, position) }

        moviesViewPager.adapter = adapter
        moviesViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        moviesViewPager.offscreenPageLimit = 1
        moviesViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val movieList = adapter?.list ?: return

                if (position == movieList.size - 3 && !isSearching && presenter.hasMorePages()) {
                    loadingProgress.visibility = View.VISIBLE

                    presenter.fetchMoreMovies(mainTopic.list_id, ++page)

                    isSearching = true
                }
            }
        })

        val horizontalSpace = 42.convertIntToDp(this)
        val spaceBetweenItems = 32.convertIntToDp(this)

        moviesViewPager.setPageTransformer(
            ScaleMovieImageTransformer(
                horizontalSpace,
                spaceBetweenItems
            )
        )

        moviesViewPager.addItemDecoration(ScaleMovieImageTransformer.HorizontalMarginItemDecoration(horizontalSpace))

        val titleColor = resources.getIdentifier(mainTopic.titleColor, "color", packageName)
        toolbar.getNavigationIcon()?.setColorFilter(resources.getColor(titleColor), PorterDuff.Mode.SRC_ATOP)

        presentationTitle.text = mainTopic.title

        presentationTitle.setTextColor(resources.getColor(titleColor))
        presentationSubtitle.setTextColor(resources.getColor(titleColor))
    }

    override fun bindMoreMovies(movies: List<Movie>) {
        loadingProgress.visibility = View.GONE

        adapter?.addMoreMovies(movies)

        isSearching = false
    }

    override fun startLoading() {
        contentContainer.visibility = View.INVISIBLE

        loadView.startShimmer()
        loadView.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        contentContainer.visibility = View.VISIBLE

        loadView.stopShimmer()
        loadView.visibility = View.GONE
    }

    private fun onMovieSelected(movie: Movie, position: Int) {
        val id = movie.id ?: return

        startActivity(MovieDetailsActivity.newIntent(this, id))
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
