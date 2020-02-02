package com.tiagohs.cinema_history.ui.activities

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.domain.presenter.MilMoviesPresentationPresenter
import com.tiagohs.cinema_history.ui.adapters.MovieListAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.adapters.decorators.ScaleMovieImageTransformer
import com.tiagohs.domain.views.MilMoviesPresentationView
import kotlinx.android.synthetic.main.activity_mil_movies_presentation.*
import javax.inject.Inject
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.getResourceColor
import com.tiagohs.helpers.extensions.startActivityWithSlideAnimation
import com.tiagohs.helpers.utils.AnimationUtils


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

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun setupArguments() {
        mainTopic = intent?.extras?.getSerializable(MAIN_TOPIC) as? MilMoviesMainTopic ?: return
    }

    override fun bindMovieList(list: List<Movie>) {
        adapter = MovieListAdapter(this, ArrayList(list), mainTopic)

        adapter?.onMovieSelected = { movie, _ -> onMovieSelected(movie) }

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

        val titleColorRes = resources.getIdentifier(mainTopic.titleColor, "color", packageName)
        val titleColor = getResourceColor(titleColorRes)

        toolbar.getNavigationIcon()?.setColorFilter(titleColor, PorterDuff.Mode.SRC_ATOP)

        presentationTitle.text = mainTopic.title

        presentationTitle.setTextColor(titleColor)
        presentationSubtitle.setTextColor(titleColor)

        presentationTitle.startAnimation(AnimationUtils.createFadeInAnimation(200, 350))
        presentationSubtitle.startAnimation(AnimationUtils.createFadeInAnimation(200, 350))
    }

    override fun bindMoreMovies(movies: List<Movie>) {
        loadingProgress.visibility = View.GONE

        adapter?.addMoreMovies(movies)

        isSearching = false
    }

    override fun startLoading() {
        contentContainer.alpha = 0f

        loadView.startShimmer()
        loadView.visibility = View.VISIBLE
        loadView.alpha = 1f
    }

    override fun hideLoading() {
        contentContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        loadView
            .animate()
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(AccelerateInterpolator(2f))
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    loadView.stopShimmer()
                    loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
            .start()
    }

    private fun onMovieSelected(movie: Movie) {
        val id = movie.id ?: return

        startActivityWithSlideAnimation(MovieDetailsActivity.newIntent(this, id))
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
