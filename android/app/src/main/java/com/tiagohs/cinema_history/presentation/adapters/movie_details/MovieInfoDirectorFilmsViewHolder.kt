package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
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
import kotlinx.android.synthetic.main.adapter_movie_info_director_films.*

class MovieInfoDirectorFilmsViewHolder(
    view: View,
    val activity: FragmentActivity,
    val onMovieClicked: ((movieId: Int) -> Unit)? = null,
    val onPersonClicked: ((personId: Int) -> Unit)?
) : BaseViewHolder<MovieInfo>(view) {

    private var isSetup = false

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie
        val context = containerView.context
        val director = movie.credits?.crew?.filter { it.job == "Director" }?.firstOrNull() ?: return

        if (!isSetup) {
            scriptsSpecialListTitle.setResourceText(
                context.getString(
                    R.string.director_collection_title,
                    director.name
                )
            )

            imageSpecial.loadImage(
                director.profilePath?.imageUrlFromTMDB(ImageSize.PROFILE_632),
                contentDescription = containerView.context.getString(
                    R.string.person_photo_description,
                    director.name
                ),
                placeholder = null,
                errorPlaceholder = null
            )

            scriptsSpecialListRecyclerView.layoutManager =
                LinearLayoutManager(containerView.context, LinearLayoutManager.HORIZONTAL, false)
            scriptsSpecialListRecyclerView.addItemDecoration(
                SpaceOffsetDecoration(
                    activity.getScreenWidth() / 2,
                    SpaceOffsetDecoration.LEFT
                )
            )

            headerViewClickable.setOnClickListener {
                val id = director.id ?: return@setOnClickListener

                onPersonClicked?.invoke(id)
            }

            scriptsSpecialListRecyclerView.adapter = MovieItemAdapter(
                movie.directorMovies ?: emptyList()
            ).apply {
                onMovieClicked = this@MovieInfoDirectorFilmsViewHolder.onMovieClicked
            }

            scriptsSpecialListRecyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val adapter = recyclerView.adapter ?: return

                    if (adapter.itemCount != 0) {
                        val lastVisibleItemPosition =
                            (recyclerView.layoutManager as? LinearLayoutManager)?.findLastCompletelyVisibleItemPosition()
                                ?: return
                        if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == 0) {
                            backgroundColor
                                .animate()
                                .alpha(0f)
                                .setDuration(400)
                                .setListener(null)
                        } else if (lastVisibleItemPosition == 1) {
                            backgroundColor
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

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_director_films
    }
}