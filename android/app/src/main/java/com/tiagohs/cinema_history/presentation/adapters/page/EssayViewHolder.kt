package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentEssay
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.adapter_page_essay.*

class EssayViewHolder(
    val view: View,
    private val appLanguage: String,
    private val onMovieClicked: ((movieId: Int) -> Unit)? = null,
    private val onPersonClicked: ((personId: Int) -> Unit)? = null,
    private val onLinkClicked: ((url: String) -> Unit)? = null
) : BasePageViewHolder(view) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = containerView.context ?: return
        val activity = context as? AppCompatActivity ?: return
        val contentEssay = item as? ContentEssay ?: return

        essayVideoViewer.setupPlayer(activity, contentEssay.videoId)
        essayVideoTitle.setResourceText(contentEssay.title)
        essayVideoDescription.setResourceText(contentEssay.description)

        setupChannel(contentEssay)
        setupContent(contentEssay)
    }

    private fun setupChannel(contentEssay: ContentEssay) {
        val essayChannel = contentEssay.channel

        if (essayChannel == null) {
            essayChannelImageCard.visibility = View.INVISIBLE
            return
        }

        essayChannelContainer.setOnClickListener {
            onLinkClicked?.invoke(essayChannel.url)
        }
    }

    private fun setupContent(contentEssay: ContentEssay) {
        if (contentEssay.movie != null) {
            setupMovieContent(contentEssay.movie!!)
        }

        if (contentEssay.person != null) {
            setupPersonContent(contentEssay.person!!)
        }
    }

    private fun setupPersonContent(person: Person) {
        essayMovieContainer.hide()
        essayPersonContainer.show()

        val knownForDepartment = person.knownForDepartment ?: ""
        val personName = person.name

        essayPersonImage.loadImage(
            person.profilePath?.imageUrlFromTMDB( ImageSize.PROFILE_185 ),
            containerView.context.getString(R.string.person_photo_description, person.name),
            R.drawable.placeholder_movie_person,
            R.drawable.placeholder_movie_person
        )
        essayPersonName.setResourceText(personName)
        essayPersonKnownFor.setResourceText(knownForDepartment)
        essayPersonContainer.setOnClickListener {
            val id = person.id ?: return@setOnClickListener

            onPersonClicked?.invoke(id)
        }
    }

    private fun setupMovieContent(movie: Movie) {
        essayPersonContainer.hide()
        essayMovieContainer.show()

        val posterUrl = movie.posterPath?.imageUrlFromTMDB( ImageSize.POSTER_342 )
        val movieName = movie.getMovieTitleFromAppLanguage(appLanguage)
        val directors = movie.credits?.crew?.filter { it.job == "Director" }?.map { it.name }
            ?.joinToString(", ") ?: ""

        essayMovieImage.loadImage(
            posterUrl,
            containerView.context.getString(R.string.movie_poster_description, movie.title),
            R.drawable.placeholder_movie_poster,
            R.drawable.placeholder_movie_poster
        )

        essayMovieName.setResourceText(movieName)
        essayMovieDirector.setResourceText(directors)
        essayMovieContainer.setOnClickListener {
            val id = movie.id ?: return@setOnClickListener

            onMovieClicked?.invoke(id)
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_page_essay
    }
}