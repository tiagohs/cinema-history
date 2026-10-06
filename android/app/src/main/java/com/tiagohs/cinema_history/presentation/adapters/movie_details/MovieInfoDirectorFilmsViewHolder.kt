package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.graphics.Color
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoDirectorFilmsBinding
import android.graphics.drawable.GradientDrawable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.MovieItemAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import com.tiagohs.helpers.utils.ColorUtils

class MovieInfoDirectorFilmsViewHolder(
    private val binding: AdapterMovieInfoDirectorFilmsBinding,
    val activity: FragmentActivity,
    val onMovieClicked: ((movieId: Int) -> Unit)? = null,
    val onPersonClicked: ((personId: Int) -> Unit)?
) : BaseViewHolder<MovieInfo>(binding) {

    private var isSetup = false

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie
        val context = itemView.context
        val colorAsset = ColorUtils.getRandomColorForDegradeAssets()
        val colorBackgroundName = "md_${colorAsset.colorName}_500"
        val degradeColorName = "background_degrade_${colorAsset.colorName}_end"
        val director = movie.credits?.crew?.filter { it.job == "Director" }?.firstOrNull() ?: return

        if (!isSetup) {
            binding.scriptsSpecialListTitle.setResourceText(
                context.getString(
                    R.string.director_collection_title,
                    director.name
                )
            )
            binding.scriptsSpecialListTitle.setResourceTextColor(colorAsset.textColorName)
            binding.backgroundColor.setResourceBackgroundColor(colorBackgroundName)
            binding.scriptsSpecialListContainer.setResourceBackgroundColor(colorBackgroundName)

            binding.degradeEnd.background = context.getResourceDrawable(degradeColorName)

            binding.imageSpecial.loadImage(
                director.profilePath?.imageUrlFromTMDB(ImageSize.PROFILE_632),
                contentDescription = itemView.context.getString(
                    R.string.person_photo_description,
                    director.name
                ),
                placeholder = null,
                errorPlaceholder = null
            )

            binding.scriptsSpecialListRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            binding.scriptsSpecialListRecyclerView.addItemDecoration(
                SpaceOffsetDecoration(
                    activity.getScreenWidth() / 2,
                    SpaceOffsetDecoration.LEFT
                )
            )

            binding.headerViewClickable.setOnClickListener {
                val id = director.id ?: return@setOnClickListener

                onPersonClicked?.invoke(id)
            }

            binding.scriptsSpecialListRecyclerView.adapter = MovieItemAdapter(
                movie.directorMovies ?: emptyList()
            ).apply {
                onMovieClicked = this@MovieInfoDirectorFilmsViewHolder.onMovieClicked
            }

            binding.scriptsSpecialListRecyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val adapter = recyclerView.adapter ?: return

                    if (adapter.itemCount != 0) {
                        val lastVisibleItemPosition =
                            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition()
                                ?: return
                        if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == 0) {
                            binding.backgroundColor
                                .animate()
                                .alpha(0f)
                                .setDuration(400)
                                .setListener(null)
                        } else if (lastVisibleItemPosition == 1) {
                            binding.backgroundColor
                                .animate()
                                .alpha(0.75f)
                                .setDuration(400)
                                .setListener(null)
                        }
                    }
                }
            })

            isSetup = true
        }

    }
}