package com.tiagohs.cinema_history.ui.activities

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.enums.MovieInfoType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.extensions.startActivityWithSlideAnimation
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.helpers.utils.DateUtils
import com.tiagohs.cinema_history.models.dto.PersonDTO
import com.tiagohs.cinema_history.models.movie_info.MovieInfo
import com.tiagohs.cinema_history.models.movie_info.MovieInfoPersonList
import com.tiagohs.cinema_history.models.tmdb.movie.Genres
import com.tiagohs.cinema_history.models.tmdb.movie.Movie
import com.tiagohs.cinema_history.presenter.MovieDetailsPresenter
import com.tiagohs.cinema_history.ui.adapters.MovieInfoAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.cinema_history.ui.views.MovieDetailsView
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.view_genre_item.view.*
import javax.inject.Inject

class MovieDetailsActivity: BaseActivity(), MovieDetailsView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_movie_details
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var presenter: MovieDetailsPresenter

    var movieId: Int = 0
    var movie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        setupToolbar(toolbar)

        presenter.onBindView(this)
        presenter.fetchMovieDetails(movieId)
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
        movieId = intent.getIntExtra(MOVIE_ID, 0)
    }

    override fun bindMovieDetails(movie: Movie) {
        val movieInfoList = generateMovieInfoList(movie)
        val adapter = MovieInfoAdapter(this, movieInfoList)

        collapsingToolbar.title = movie.title ?: movie.originalTitle
        adapter.onPersonClicked = { onPersonClicked(it) }

        pageContentList.adapter = adapter
        pageContentList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bindMovieHeader(movie)
    }

    override fun startLoading() {
        pageContentListContainer.alpha = 0f
        appBar.alpha = 0f

        loadView.startShimmer()
        loadView.visibility = View.VISIBLE
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
                    loadView.stopShimmer()
                    loadView.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}

            })
            .start()
    }

    private fun generateMovieInfoList(movie: Movie): List<MovieInfo> {
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

        return listOf(
            MovieInfo(MovieInfoType.INFO_HEADER, movie),
            MovieInfo(MovieInfoType.INFO_SUMMARY, movie),
            MovieInfoPersonList(MovieInfoType.INFO_CAST, movie, castList, "Elenco"),
            MovieInfoPersonList(MovieInfoType.INFO_CREW, movie, crewList, "Equipe Técnica")
        )
    }

    private fun onPersonClicked(personId: Int) {
        startActivityWithSlideAnimation(PersonDetailsActivity.newIntent(this, personId))
    }

    private fun bindMovieHeader(movie: Movie) {
        val genres = movie.genres
        val backdropPath = movie.backdropPath?.imageUrlFromTMDB(ImageSize.BACKDROP_300)

        movieTitle.text = movie.title
        movieOriginalTitle.text = "${movie.originalTitle} (${DateUtils.getYearByDate(movie.releaseDate)})"

        bindGenres(genres)
        bindTrailer(movie)
        bindBackdrop(backdropPath)
    }

    private fun bindGenres(genres: List<Genres>?) {
        genres?.let {
            genresScrollView.visibility = View.VISIBLE

            it.forEach { genre ->
                val view = LayoutInflater.from(this).inflate(R.layout.view_genre_item, null, false)
                val layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT)

                layoutParams.setMargins(0, 0, 10.convertIntToDp(this), 0)
                view.genreName.text = genre.name

                view.layoutParams = layoutParams
                genresContainer.addView(view)
            }
        }
    }

    private fun bindTrailer(movie: Movie) {
        playContainer.setOnClickListener {
            val trailerUrl = movie.videos?.videoList?.find { it.type == "Trailer" }?.key ?: movie.videos?.videoList?.firstOrNull()?.key

            trailerUrl?.let {
                openUrl("https://www.youtube.com/watch?v=${it}")
            }
        }
    }

    private fun bindBackdrop(backdropPath: String?) {
        movieBackdrop.loadImage(backdropPath, R.drawable.placeholder_movie_poster, R.drawable.placeholder_movie_poster) {
            movieBackdrop.alpha = 1f

            AnimationUtils.createShowCircularReveal(movieBackdrop) {
                playCard.alpha = 1f

                val animation = AnimationUtils.createFadeInAnimation(150) {
                    movieBackdropDegrade.alpha = 1f
                    genresScrollView.alpha = 1f
                }

                movieBackdropDegrade.startAnimation(animation)

                AnimationUtils.createPulseAnimation(movieTitle)
                AnimationUtils.createPulseAnimation(movieOriginalTitle)


                AnimationUtils.createScaleUpAnimation(playCard, 0f, 1f, 0f, 1f, 0.5f, 0.5f, 200)
            }
        }
    }

    companion object {

        const val MOVIE_ID = "MOVIE_ID"

        fun newIntent(context: Context, movieId: Int): Intent {
            val intent = Intent(context, MovieDetailsActivity::class.java)

            intent.putExtra(MOVIE_ID, movieId)

            return intent
        }

    }
}