package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.tools.SpaceOffsetDecoration
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.adapter_movie_special_item.*
import kotlinx.android.synthetic.main.view_line_five_colors.*
import kotlin.math.abs

class MovieItemSpecialAdapter(
    list: List<MovieFilmographyDTO>
) : BaseAdapter<MovieFilmographyDTO, MovieItemSpecialAdapter.MovieItemViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    var onMovieClicked: ((movieId: Int) -> Unit)? = null


    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_movie_special_item

    override fun onCreateViewHolder(viewType: Int, view: View): MovieItemViewHolder =
        MovieItemViewHolder(view)

    override fun getItemId(position: Int): Long = list[position].id?.toLong() ?: position.toLong()

    inner class MovieItemViewHolder(view: View) : BaseViewHolder<MovieFilmographyDTO>(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val interpolator = FastOutLinearInInterpolator()

        /**
         * Offset the thumb and text with a factor [-1.0..1.0] of the total width
         */
        var offset: Float = 0f
            set(v) {
                field = v.coerceIn(-1f, 1f)

                val direction = if (field < 0) -1f else 1f
                val interpolatedValue = interpolator.getInterpolation(abs(field))
                val translationX = direction * interpolatedValue * itemView.measuredWidth

                movieName.translationX = translationX
                character.translationX = translationX
                posterCard.translationX = translationX
            }

        override fun bind(item: MovieFilmographyDTO, position: Int) {
            super.bind(item, position)

            val colorAsset = ColorUtils.getRandomColorAssets()

            movieName.setResourceText(item.title)
            overview.setResourceText(item.overview)

            bindCharacters(item.character)
            bindDepartaments(item.departments, colorAsset)

            moviePoster.loadImage(item.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_342), contentDescription = containerView.context.getString(R.string.movie_poster_description, item.title))
            movieBackdrop.loadImage(item.backdrop?.imageUrlFromTMDB(ImageSize.BACKDROP_300), contentDescription = containerView.context.getString(R.string.movie_backdrop_description, item.title))

            bindColor(colorAsset)
        }

        private fun bindColor(colorAsset: com.tiagohs.entities.ColorAsset) {
            val context = itemView.context ?: return
            val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")
            val textColor = context.getResourceColor(colorAsset.textColorName)

            itemContainerCard.setCardBackgroundColor(backgroundColor)

            movieName.setTextColor(textColor)
            character.setTextColor(textColor)
            overview.setTextColor(textColor)

            color1.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_500"))
            color2.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_600"))
            color3.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_700"))
            color4.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_800"))
            color5.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_900"))
        }

        private fun bindCharacters(characters: String?) {

            if (!characters.isNullOrBlank()) {
                character.visibility = View.VISIBLE
                character.text = itemView.context.getString(R.string.as_format, characters)
            } else {
                character.hide()
            }
        }

        private fun bindDepartaments(
            departaments: String?,
            colorAsset: com.tiagohs.entities.ColorAsset
        ) {

            if (!departaments.isNullOrBlank()) {
                val context = itemView.context ?: return
                val listOfDepartments =
                    departaments.split(",").map { it.trim() }.filter { it.isNotBlank() }
                val textColor = context.getResourceColor(colorAsset.textColorName)

                departments.show()

                departments.apply {
                    adapter = DepartamentAdapter(listOfDepartments, textColor)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    addItemDecoration(
                        SpaceOffsetDecoration(
                            10.convertIntToDp(context),
                            SpaceOffsetDecoration.LEFT
                        )
                    )
                }
            } else {
                departments.hide()
            }
        }

        override fun onClick(v: View?) {
            val movieId = item?.id ?: return

            onMovieClicked?.invoke(movieId)
        }
    }

}