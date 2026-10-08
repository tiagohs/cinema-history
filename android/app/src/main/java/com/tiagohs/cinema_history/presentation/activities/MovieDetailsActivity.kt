package com.tiagohs.cinema_history.presentation.activities

import android.animation.Animator
import com.tiagohs.cinema_history.databinding.ActivityMovieDetailsBinding
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ViewGenreItemBinding
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.ads.AdPlacement
import com.tiagohs.cinema_history.ads.adapterWithNativeAd
import com.tiagohs.cinema_history.presentation.adapters.MovieInfoAdapter
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.domain.managers.DynamicLinkManager
import com.tiagohs.domain.managers.SettingsManager
import com.tiagohs.domain.presenter.MovieDetailsPresenter
import com.tiagohs.domain.views.MovieDetailsView
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.MessageViewType
import com.tiagohs.entities.enums.MovieInfoType
import com.tiagohs.entities.enums.RatingType
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.movie_info.MovieInfoPersonList
import com.tiagohs.entities.tmdb.movie.Genres
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.helpers.Constants
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.AnimationUtils
import com.tiagohs.helpers.utils.DateUtils
import javax.inject.Inject

class MovieDetailsActivity : BaseActivity<ActivityMovieDetailsBinding>(), MovieDetailsView {

    override fun inflateBinding(inflater: LayoutInflater) = ActivityMovieDetailsBinding.inflate(inflater)
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

        setupToolbar(binding.toolbar)

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
        val movieTitle = movie?.getMovieTitleFromAppLanguage(settingManager.getMovieLanguage())

        shareContent(
            getString(R.string.share_history_page_description, movieTitle, shorLink),
            getResourceString(
                R.string.share_title
            )
        )

        hideScreenBlocked()
    }

    private fun onBuildPageLinkError(exception: Exception) {
        hideScreenBlocked()

        onError(exception, R.string.unknown_error, MessageViewType.ERROR, Snackbar.LENGTH_SHORT)
    }

    fun showScreenBlocked() {
        binding.screenBlocked.root.show()
    }

    fun hideScreenBlocked() {
        binding.screenBlocked.root.hide()
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

        //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun setupArguments() {
        movieId = intent.getIntExtra(MOVIE_ID, 0)
        isFromUniversalLink = intent.getBooleanExtra(Constants.IS_FROM_UNIVERSAL_LINK, false)
    }

    override fun bindMovieDetails(movie: Movie) {
        this.movie = movie

        val movieInfoList = generateMovieInfoList(movie)
        val movieTitle = movie.getMovieTitleFromAppLanguage(settingManager.getMovieLanguage())
        val appLanguage = settingManager.getMovieLanguage()

        binding.collapsingToolbar.title = movieTitle
        binding.pageContentList.apply {
            // Anúncio nativo depois de "Onde assistir" (ou da sinopse, se o filme não tiver onde assistir).
            val watchOnIndex = movieInfoList.indexOfFirst { it.type == MovieInfoType.INFO_WATCH_ON }
            val summaryIndex = movieInfoList.indexOfFirst { it.type == MovieInfoType.INFO_SUMMARY }
            val adAfter = ((if (watchOnIndex >= 0) watchOnIndex else summaryIndex) + 1).coerceAtLeast(2)

            adapter = adapterWithNativeAd(movieInfoList, adAfter, AdPlacement.MOVIE, this@MovieDetailsActivity) { items ->
                MovieInfoAdapter(items, this@MovieDetailsActivity, appLanguage).apply {
                    onPersonClicked = { onPersonClicked(it) }
                    onExtenalLink = { openLink(it) }
                    onVideoClick = { openLink(getString(R.string.youtube_link, it)) }
                    onMovieClicked = { onMovieSelected(it) }
                    onScreenLink = { startActivityWithSlideRightToLeftAnimation(it) }
                }
            }
            layoutManager = LinearLayoutManager(
                this@MovieDetailsActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        bindMovieHeader(movie, movieTitle)
    }

    private fun onMovieSelected(movieId: Int) {
        startActivityWithSlideRightToLeftAnimation(newIntent(this, movieId))
    }

    override fun startLoading() {
        binding.pageContentListContainer.alpha = 0f
        binding.appBar.alpha = 0f

        binding.loadView.showShimmer(true)
        binding.loadView.show()
    }

    override fun hideLoading() {
        binding.pageContentListContainer
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        binding.appBar
            .animate()
            .alpha(1f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator(2f))
            .start()

        binding.loadView
            .animate()
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(AccelerateInterpolator(2f))
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    binding.loadView.hideShimmer()
                    binding.loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationStart(animation: Animator) {}

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

        movie.extraInfo?.historyMainTopic?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_HISTORY,
                    movie
                )
            )
        }

        movie.extraInfo?.watchOn?.takeIf { it.isNotEmpty() }?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_WATCH_ON,
                    movie
                )
            )
        }
        movie.extraInfo?.blockSpecial?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_BLOCK_SPECIAL,
                    movie
                )
            )
        }

        if (castList.isNotEmpty()) {
            listOfMovieList.add(
                MovieInfoPersonList(
                    MovieInfoType.INFO_CAST, movie, castList, getResourceString(
                        R.string.cast
                    )
                )
            )
        }

        if (crewList.isNotEmpty()) {
            listOfMovieList.add(
                MovieInfoPersonList(
                    MovieInfoType.INFO_CREW, movie, crewList, getResourceString(
                        R.string.crew
                    )
                )
            )
        }

        movie.extraInfo?.quote?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_QUOTE,
                    movie
                )
            )
        }

        movie.extraInfo?.milMoviesMainTopic?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_MIL_MOVIES,
                    movie
                )
            )
        }

        movie.extraInfo?.reviewResults?.takeIf { it.isNotEmpty() }?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_REVIEWS,
                    movie
                )
            )
        }
        movie.directorMovies?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_DIRECTORS_MOVIE,
                    movie
                )
            )
        }

        if (!movie.allImages.isNullOrEmpty() || !movie.videos?.videoList.isNullOrEmpty()) {
            listOfMovieList.add(MovieInfo(MovieInfoType.INFO_MIDIAS, movie))
        }

        movie.extraInfo?.didYouKnowList?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_DID_YOUT_KNOW,
                    movie
                )
            )
        }

        if (!movie.productionCompanies.isNullOrEmpty()) {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_PRODUCTION,
                    movie
                )
            )
        }

        movie.movieCollection?.let {
            listOfMovieList.add(
                MovieInfo(
                    MovieInfoType.INFO_COLLECTION,
                    movie
                )
            )
        }

        return listOfMovieList
    }

    private fun onPersonClicked(personId: Int) {
        startActivityWithSlideRightToLeftAnimation(PersonDetailsActivity.newIntent(this, personId))
    }

    private fun bindMovieHeader(movie: Movie, title: String) {
        val genres = movie.genres

        binding.movieTitle.setResourceText(title)
        binding.movieOriginalTitle.text = getString(
            R.string.original_title_format, movie.originalTitle, DateUtils.getYearByDate(
                movie.releaseDate
            )
        )

        bindGenres(genres)
        bindTrailer(movie)
        bindBackdrop(movie, title)
        bindRatings(movie)
    }

    private fun bindGenres(genres: List<Genres>?) {
        genres?.let {
            binding.genresScrollView.show()

            it.forEach { genre ->
                val genreBinding = ViewGenreItemBinding.inflate(LayoutInflater.from(this), null, false)
                val layoutParams = Constraints.LayoutParams(
                    Constraints.LayoutParams.WRAP_CONTENT,
                    Constraints.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 10.convertIntToDp(this@MovieDetailsActivity), 0)
                }

                genreBinding.root.layoutParams = layoutParams
                genreBinding.genreName.setResourceText(genre.name)

                binding.genresContainer.addView(genreBinding.root)
            }
        }
    }

    private fun bindTrailer(movie: Movie) {
        val trailerUrlKey = movie.trailerUrlKey

        if (movie.trailerUrlKey.isNullOrBlank()) {
            binding.playContainer.hide()
            binding.separatorVertical.setGuidelinePercent(1f)
            return
        }

        binding.playContainer.setOnClickListener {
            openLink(
                getString(
                    R.string.youtube_link,
                    trailerUrlKey
                )
            )
        }
    }

    private fun bindBackdrop(movie: Movie, title: String) {
        val backdropPath = movie.backdropPath?.imageUrlFromTMDB(ImageSize.BACKDROP_780)

        binding.movieBackdrop.loadImage(
            backdropPath,
            getString(R.string.movie_backdrop_description, title),
            R.drawable.placeholder_movie_poster,
            R.drawable.placeholder_movie_poster
        ) {
            binding.movieBackdrop.alpha = 1f

            AnimationUtils.createShowCircularReveal(binding.movieBackdrop) {
                binding.playCard.alpha = 1f

                val animation = AnimationUtils.createFadeInAnimation(150) {
                    binding.movieBackdropDegrade.alpha = 1f
                    binding.genresScrollView.alpha = 1f
                    binding.movieBackdropDegradeTop.alpha = 1f
                }

                binding.movieBackdropDegrade.startAnimation(animation)
                binding.movieBackdropDegradeTop.startAnimation(animation)

                AnimationUtils.createPulseAnimation(binding.movieTitle)
                AnimationUtils.createPulseAnimation(binding.movieOriginalTitle)

                AnimationUtils.createScaleUpAnimation(binding.playCard, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200)
            }
        }
    }

    private fun bindRatings(movie: Movie) {
        movie.omdbResult?.ratings?.forEach { omdbResult ->
            when (omdbResult.source) {
                RatingType.INTERNET_MOVIE_DATABASE -> bindRating(
                    omdbResult.value,
                    getString(R.string.imdb_link, movie.externalIds?.imdbId),
                    binding.imdbRating,
                    binding.imdbContainer
                )
                RatingType.METACRITIC -> bindRating(
                    omdbResult.value,
                    getResourceString(R.string.metacritic_link),
                    binding.metacriticRating,
                    binding.metacriticContainer
                )
                RatingType.TOMATOES -> bindRating(
                    omdbResult.value,
                    getResourceString(R.string.tomatoes_link),
                    binding.tomatoesRating,
                    binding.tomatoesContainer
                )
                else -> {
                }
            }
        }
        val voteAverage = movie.voteAverage
        if (voteAverage != null && voteAverage > 0) {
            bindRating(
                getString(
                    R.string.tmdb_vote,
                    String.format("%.1f", voteAverage)
                ),
                getString(R.string.tmdb_link, movie.id),
                binding.tmdbRating, binding.tmdbContainer
            )
            return
        }

        binding.tmdbContainer.hide()
    }

    private fun bindRating(
        ratingValue: String?,
        textLink: String,
        textView: TextView,
        containerView: View
    ) {
        ratingValue?.let {
            containerView.show()
            textView.setResourceText(it)

            containerView.setOnClickListener { openLink(textLink) }
        } ?: containerView.hide()
    }

    companion object {

        const val MOVIE_ID = "MOVIE_ID"
        const val MAIN_TOPIC = "MAIN_TOPIC"

        fun newIntent(
            context: Context,
            movieId: Int,
            isFromUniversalLink: Boolean = false
        ): Intent {
            val intent = Intent(context, MovieDetailsActivity::class.java)

            intent.putExtra(MOVIE_ID, movieId)
            intent.putExtra(Constants.IS_FROM_UNIVERSAL_LINK, isFromUniversalLink)

            return intent
        }

    }
}