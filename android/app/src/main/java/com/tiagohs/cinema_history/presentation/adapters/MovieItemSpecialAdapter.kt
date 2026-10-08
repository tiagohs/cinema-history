package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterMovieSpecialItemBinding
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
import kotlin.math.abs

class MovieItemSpecialAdapter(
    list: List<MovieFilmographyDTO>
) : BaseAdapter<MovieFilmographyDTO, MovieItemSpecialAdapter.MovieItemViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    var onMovieClicked: ((movieId: Int) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): MovieItemViewHolder =
        MovieItemViewHolder(AdapterMovieSpecialItemBinding.inflate(inflater, parent, false))

    override fun getItemId(position: Int): Long = list[position].id?.toLong() ?: position.toLong()

    inner class MovieItemViewHolder(private val binding: AdapterMovieSpecialItemBinding) : BaseViewHolder<MovieFilmographyDTO>(binding),
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

                binding.movieName.translationX = translationX
                binding.character.translationX = translationX
                binding.posterCard.translationX = translationX
            }

        override fun bind(item: MovieFilmographyDTO, position: Int) {
            super.bind(item, position)

            val colorAsset = ColorUtils.getRandomColorAssets()

            binding.movieName.setResourceText(item.title)
            binding.overview.setResourceText(item.overview)

            bindCharacters(item.character)
            bindDepartaments(item.departments, colorAsset)

            binding.moviePoster.loadImage(item.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_342), contentDescription = itemView.context.getString(R.string.movie_poster_description, item.title))
            binding.movieBackdrop.loadImage(item.backdrop?.imageUrlFromTMDB(ImageSize.BACKDROP_300), contentDescription = itemView.context.getString(R.string.movie_backdrop_description, item.title))

            bindColor(colorAsset)
        }

        private fun bindColor(colorAsset: com.tiagohs.entities.ColorAsset) {
            val context = itemView.context ?: return
            val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")
            val textColor = context.getResourceColor(colorAsset.textColorName)

            binding.itemContainerCard.setCardBackgroundColor(backgroundColor)

            binding.movieName.setTextColor(textColor)
            binding.character.setTextColor(textColor)
            binding.overview.setTextColor(textColor)

            binding.viewLineFiveColors.color1.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_500"))
            binding.viewLineFiveColors.color2.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_600"))
            binding.viewLineFiveColors.color3.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_700"))
            binding.viewLineFiveColors.color4.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_800"))
            binding.viewLineFiveColors.color5.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_900"))
        }

        private fun bindCharacters(characters: String?) {

            if (!characters.isNullOrBlank()) {
                binding.character.visibility = View.VISIBLE
                binding.character.text = itemView.context.getString(R.string.as_format, characters)
            } else {
                binding.character.hide()
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

                binding.departments.show()

                binding.departments.apply {
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
                binding.departments.hide()
            }
        }

        override fun onClick(v: View?) {
            val movieId = item?.id ?: return

            onMovieClicked?.invoke(movieId)
        }
    }

}