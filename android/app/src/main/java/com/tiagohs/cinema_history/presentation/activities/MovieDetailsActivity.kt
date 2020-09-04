package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.helpers.utils.DateUtils
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.movie_info.MovieInfoPersonList
import com.tiagohs.entities.tmdb.movie.Genres
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.domain.presenter.MovieDetailsPresenter
import com.tiagohs.cinema_history.presentation.adapters.MovieInfoAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.MovieInfoType
import com.tiagohs.domain.views.MovieDetailsView
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.helpers.Constants
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.view_genre_item.view.*
import kotlinx.android.synthetic.main.view_screen_blocked.*
import java.lang.Exception
import javax.inject.Inject

class MovieDetailsActivity: BaseActivity(), MovieDetailsView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_movie_details
    override fun onGetMenuLayoutId(): Int = R.menu.menu_movie

    @Inject
    lateinit var dynamicLinkManager: DynamicLinkManager

    @Inject
    lateinit var presenter: MovieDetailsPresenter

    @Inject
    lateinit var settingManager: SettingsManager

    var movieId: Int = 0
    var movie: Movie? = null
    private var isFromUniversalLink = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        setupToolbar(toolbar)

        presenter.onBindView(this)
        presenter.fetchMovieDetails(movieId, settingManager.getMovieISOLanguage())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_share -> {
                onShareClicked()
                return true
            }
            else -> return false
        }

    }

    private fun onShareClicked() {
        showScreenBlocked()

        dynamicLinkManager.buildMoviePageLink(
            movieId,
            onComplete = { onBuildPageLinkComplete(it) },
            onError = { onBuildPageLinkError(it) }
        )
    }

    private fun onBuildPageLinkComplete(shorLink: String) {
        val movieTitle = movie?.getMovieTitleFromAppLanguage(settingManager.getMovieISOLanguage())

        shareContent(getString(R.string.share_history_page_description, movieTitle, shorLink), getResourceString(R.string.share_title))

        hideScreenBlocked()
    }

    private fun onBuildPageLinkError(exception: Exception) {
        hideScreenBlocked()

        onError(exception, R.string.unknown_error, MessageViewType.ERROR, Snackbar.LENGTH_SHORT)
    }

    fun showScreenBlocked() {
        screenBlocked.show()
    }

    fun hideScreenBlocked() {
        screenBlocked.hide()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onUnbindView()
    }

    override fun onBackPressed() {
        if (isFromUniversalLink) {
            startActivityWithSlideRightToLeftAnimation(HomeActivity.newIntent(this))
            finish()
            return
        }
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun setupArguments() {
        movieId = intent.getIntExtra(MOVIE_ID, 0)
        isFromUniversalLink = intent.getBooleanExtra(Constants.IS_FROM_UNIVERSAL_LINK, false)
    }

    override fun bindMovieDetails(movie: Movie) {
        this.movie = movie

        val movieInfoList = generateMovieInfoList(movie)
        val movieTitle = movie.getMovieTitleFromAppLanguage(settingManager.getMovieISOLanguage())
        val appLanguage = settingManager.getMovieISOLanguage()

        collapsingToolbar.title = movieTitle
        pageContentList.apply {
            adapter = MovieInfoAdapter(movieInfoList, this@MovieDetailsActivity, appLanguage).apply {
                onPersonClicked = { onPersonClicked(it) }
                onExtenalLink = { openLink(it) }
                onVideoClick = { openLink(getString(R.string.youtube_link,it)) }
                onMovieClicked = { onMovieSelected(it) }
            }
            layoutManager = LinearLayoutManager(this@MovieDetailsActivity, LinearLayoutManager.VERTICAL, false)
        }

        bindMovieHeader(movie, movieTitle)
    }

    private fun onMovieSelected(movieId: Int) {
        startActivityWithSlideRightToLeftAnimation(newIntent(this, movieId))
    }

    override fun startLoading() {
        pageContentListContainer.alpha = 0f
        appBar.alpha = 0f

        loadView.showShimmer(true)
        loadView.show()
    }

    override fun hideLoading() {
        pageContentListContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        appBar
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
                    loadView.hideShimmer()
                    loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
            .start()
    }

    private fun generateMovieInfoList(movie: Movie): List<MovieInfo> {
        val listOfMovieList = ArrayList<MovieInfo>()
        val castList = movie.credits?.cast?.map {
            PersonDTO(
                it.id,
                it.profilePath,
                it.name,
                it.character
            )
        } ?: emptyList()
        val crewList = movie.credits?.crew?.map {
            PersonDTO(
                it.id,
                it.profilePath,
                it.name,
                it.department
            )
        } ?: emptyList()

        listOfMovieList.add(MovieInfo(MovieInfoType.INFO_HEADER, movie))
        listOfMovieList.add(MovieInfo(MovieInfoType.INFO_SUMMARY, movie))

        if (castList.isNotEmpty()) {
            listOfMovieList.add(MovieInfoPersonList(MovieInfoType.INFO_CAST, movie, castList, getResourceString(R.string.cast)))
        }

        if (crewList.isNotEmpty()) {
            listOfMovieList.add(MovieInfoPersonList(MovieInfoType.INFO_CREW, movie, crewList, getResourceString(R.string.crew)))
        }

        movie.extraInfo?.reviewResults?.let { listOfMovieList.add(MovieInfo(MovieInfoType.INFO_REVIEWS, movie)) }
        movie.directorMovies?.let { listOfMovieList.add(MovieInfo(MovieInfoType.INFO_DIRECTORS_MOVIE, movie)) }

        movie.productionCompanies?.let { listOfMovieList.add(MovieInfo(MovieInfoType.INFO_PRODUCTION, movie)) }

        if (!movie.allImages.isNullOrEmpty() || !movie.videos?.videoList.isNullOrEmpty()) {
            listOfMovieList.add(MovieInfo(MovieInfoType.INFO_MIDIAS, movie))
        }

        movie.movieCollection?.let { listOfMovieList.add(MovieInfo(MovieInfoType.INFO_COLLECTION, movie)) }

        return listOfMovieList
    }

    private fun onPersonClicked(personId: Int) {
        startActivityWithSlideRightToLeftAnimation(PersonDetailsActivity.newIntent(this, personId))
    }

    private fun bindMovieHeader(movie: Movie, title: String) {
        val genres = movie.genres

        movieTitle.setResourceText(title)
        movieOriginalTitle.text = getString(R.string.original_title_format, movie.originalTitle, DateUtils.getYearByDate(movie.releaseDate))

        bindGenres(genres)
        bindTrailer(movie)
        bindBackdrop(movie, title)
    }

    private fun bindGenres(genres: List<Genres>?) {
        genres?.let {
            genresScrollView.show()

            it.forEach { genre ->
                val view = LayoutInflater.from(this).inflate(R.layout.view_genre_item, null, false)
                val layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 0, 10.convertIntToDp(this@MovieDetailsActivity), 0)
                }

                view.layoutParams = layoutParams
                view.genreName.setResourceText(genre.name)

                genresContainer.addView(view)
            }
        }
    }

    private fun bindTrailer(movie: Movie) {
        val trailerUrlKey = movie.trailerUrlKey

        if (movie.trailerUrlKey.isNullOrBlank()) {
            playContainer.hide()
            separatorVertical.setGuidelinePercent(1f)
            return
        }

        playContainer.setOnClickListener { openLink(getString(R.string.youtube_link, trailerUrlKey)) }
    }

    private fun bindBackdrop(movie: Movie, title: String) {
        val backdropPath = movie.backdropPath?.imageUrlFromTMDB(ImageSize.BACKDROP_780)

        movieBackdrop.loadImage(backdropPath, getString(R.string.movie_backdrop_description, title), R.drawable.placeholder_movie_poster, R.drawable.placeholder_movie_poster) {
            movieBackdrop.alpha = 1f

            AnimationUtils.createShowCircularReveal(movieBackdrop) {
                playCard.alpha = 1f

                val animation = AnimationUtils.createFadeInAnimation(150) {
                    movieBackdropDegrade.alpha = 1f
                    genresScrollView.alpha = 1f
                    movieBackdropDegradeTop.alpha = 1f
                }

                movieBackdropDegrade.startAnimation(animation)
                movieBackdropDegradeTop.startAnimation(animation)

                AnimationUtils.createPulseAnimation(movieTitle)
                AnimationUtils.createPulseAnimation(movieOriginalTitle)

                AnimationUtils.createScaleUpAnimation(playCard, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200)
            }
        }
    }

    companion object {

        const val MOVIE_ID = "MOVIE_ID"

        fun newIntent(context: Context, movieId: Int, isFromUniversalLink: Boolean = false): Intent {
            val intent = Intent(context, MovieDetailsActivity::class.java)

            intent.putExtra(MOVIE_ID, movieId)
            intent.putExtra(Constants.IS_FROM_UNIVERSAL_LINK, isFromUniversalLink)

            return intent
        }

    }
}