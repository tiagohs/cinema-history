package com.tiagohs.cinema_history.ui.fragments

import android.os.Bundle
import android.view.View
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils
import com.tiagohs.cinema_history.helpers.utils.DateUtils
import com.tiagohs.cinema_history.helpers.utils.LocaleUtils
import com.tiagohs.cinema_history.helpers.utils.MovieUtils
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import com.tiagohs.cinema_history.models.tmdb.Movie
import com.tiagohs.cinema_history.ui.configs.BaseFragment
import kotlinx.android.synthetic.main.fragment_movie_presentation_item.*

class MoviePresentationItemFragment: BaseFragment() {

    override fun getViewID(): Int = R.layout.fragment_movie_presentation_item

    override fun onErrorAction() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startWithAnimation = arguments?.getBoolean(START_WITH_ANIMATION) ?: false

        if (startWithAnimation) {
            scaleUpImage(0)
        }

        val mainTopic = arguments?.getSerializable(MAIN_TOPIC) as? MilMoviesMainTopic ?: return
        val position = arguments?.getSerializable(POSITION) as? Int ?: return

        if (position == 0) {
            val context = context ?: return
            val backgroundColor = context.resources
                .getIdentifier(mainTopic.backgroundColor, "color", context.packageName)
            backgroundContent.setBackgroundColor(context.resources.getColor(backgroundColor))
        }

        val movie = arguments?.getSerializable(MOVIE_ARGUMENT) as? Movie ?: return

        Picasso.get()
            .load(movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_500))
            .into(image)

        title.text = movie.title ?: movie.originalTitle
        originalTitle.text = movie.originalTitle

        val lang = LocaleUtils.getLanguageName(movie.originalLanguage)
        if (movie.originalLanguage != null && lang != null) {
            language.text = lang.capitalize()
        } else {
            language.visibility = View.GONE
        }

        val genreList = MovieUtils.getGenresName(context, movie.genreIds)
        if (genreList.isNotEmpty()) {
            genre.text = genreList.first()
        } else {
            genre.visibility = View.GONE
        }

    }

    fun scaleUpImage(duration: Int = 380) {
        if (imageContainer != null) {
            AnimationUtils.creatScaleUpAnimation(
                imageCard,
                1f,
                1.2f,
                1f,
                1.2f,
                0.3f,
                0.7f, duration)
        }
    }

    fun scaleDownImage() {

        if (imageContainer != null) {
            AnimationUtils.creatScaleUpAnimation(
                imageCard,
                1.2f,
                1f,
                1.2f,
                1f,
                0.3f,
                0.7f,
                380)
        }
    }

    companion object {

        const val MOVIE_ARGUMENT        = "MOVIE_ARGUMENT"
        const val START_WITH_ANIMATION  = "START_WITH_ANIMATION"
        const val MAIN_TOPIC            = "MAIN_TOPIC"
        const val POSITION              = "POSITION"

        fun newInstance(movie: Movie, mainTopic: MilMoviesMainTopic, position: Int = 0, startWithAnimation: Boolean = false): MoviePresentationItemFragment {
            val fragment = MoviePresentationItemFragment()
            val bundle = Bundle()

            bundle.putSerializable(MOVIE_ARGUMENT, movie)
            bundle.putBoolean(START_WITH_ANIMATION, startWithAnimation)
            bundle.putSerializable(MAIN_TOPIC, mainTopic)
            bundle.putInt(POSITION, position)

            fragment.arguments = bundle

            return fragment
        }
    }
}