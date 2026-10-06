package com.tiagohs.cinema_history.presentation.adapters.page

import android.view.View
import com.tiagohs.cinema_history.databinding.AdapterPageEssayBinding
import androidx.appcompat.app.AppCompatActivity
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.ColorAsset
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentEssay
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ColorUtils

class EssayViewHolder(
    private val binding: AdapterPageEssayBinding,
    private val appLanguage: String,
    private val onMovieClicked: ((movieId: Int) -> Unit)? = null,
    private val onPersonClicked: ((personId: Int) -> Unit)? = null,
    private val onLinkClicked: ((url: String) -> Unit)? = null
) : BasePageViewHolder(binding) {

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val activity = context as? AppCompatActivity ?: return
        val contentEssay = item as? ContentEssay ?: return
        val colorAsset = ColorUtils.getRandomColorAssets()
        val colorName = "md_${colorAsset.colorName}_500"

        binding.essayContainer.setResourceBackgroundColor(colorName)

        binding.essayVideoViewer.setupPlayer(activity, contentEssay.videoId)
        binding.essayVideoTitle.setResourceText(contentEssay.title)
        binding.essayVideoDescription.setResourceText(contentEssay.description)
        binding.essayVideoTitle.setResourceTextColor(colorAsset.textColorName)
        binding.essayVideoDescription.setResourceTextColor(colorAsset.textColorName)

        setupChannel(contentEssay)
        setupContent(contentEssay, colorAsset)
    }

    private fun setupChannel(contentEssay: ContentEssay) {
        val essayChannel = contentEssay.channel

        if (essayChannel == null) {
            binding.essayChannelImageCard.visibility = View.INVISIBLE
            return
        }

        binding.essayChannelImage.loadImage(contentEssay.channel?.imagePath)
        binding.essayChannelContainer.setOnClickListener {
            onLinkClicked?.invoke(essayChannel.url)
        }
    }

    private fun setupContent(contentEssay: ContentEssay, colorAsset: ColorAsset) {
        if (contentEssay.movie != null) {
            setupMovieContent(contentEssay.movie!!, colorAsset)
        } else {
            binding.essayMovieContainer.hide()
        }

        if (contentEssay.person != null) {
            setupPersonContent(contentEssay.person!!, colorAsset)
        } else {
            binding.essayPersonContainer.hide()
        }
    }

    private fun setupPersonContent(person: Person, colorAsset: ColorAsset) {
        binding.essayMovieContainer.hide()
        binding.essayPersonContainer.show()

        val knownForDepartment = person.knownForDepartment ?: ""
        val personName = person.name

        binding.essayPersonImage.loadImage(
            person.profilePath?.imageUrlFromTMDB( ImageSize.PROFILE_185 ),
            itemView.context.getString(R.string.person_photo_description, person.name),
            R.drawable.placeholder_movie_person,
            R.drawable.placeholder_movie_person
        )
        binding.essayPersonName.setResourceText(personName)
        binding.essayPersonKnownFor.setResourceText(knownForDepartment)
        binding.essayPersonName.setResourceTextColor(colorAsset.textColorName)
        binding.essayPersonKnownFor.setResourceTextColor("md_${colorAsset.colorName}_100")

        binding.essayPersonContainer.setOnClickListener {
            val id = person.id ?: return@setOnClickListener

            onPersonClicked?.invoke(id)
        }
    }

    private fun setupMovieContent(movie: Movie, colorAsset: ColorAsset) {
        binding.essayPersonContainer.hide()
        binding.essayMovieContainer.show()

        val posterUrl = movie.posterPath?.imageUrlFromTMDB( ImageSize.POSTER_342 )
        val movieName = movie.getMovieTitleFromAppLanguage(appLanguage)
        val directors = movie.credits?.crew?.filter { it.job == "Director" }?.map { it.name }
            ?.joinToString(", ") ?: ""

        binding.essayMovieImage.loadImage(
            posterUrl,
            itemView.context.getString(R.string.movie_poster_description, movie.title),
            R.drawable.placeholder_movie_poster,
            R.drawable.placeholder_movie_poster
        )

        binding.essayMovieName.setResourceText(movieName)
        binding.essayMovieDirector.setResourceText(directors)
        binding.essayMovieName.setResourceTextColor(colorAsset.textColorName)
        binding.essayMovieDirector.setResourceTextColor("md_${colorAsset.colorName}_100")

        binding.essayMovieContainer.setOnClickListener {
            val id = movie.id ?: return@setOnClickListener

            onMovieClicked?.invoke(id)
        }
    }
}