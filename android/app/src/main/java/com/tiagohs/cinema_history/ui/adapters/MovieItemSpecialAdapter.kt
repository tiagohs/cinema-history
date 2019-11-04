package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.getResourceColor
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.helpers.tools.SpaceOffsetDecoration
import com.tiagohs.cinema_history.helpers.utils.ColorAsset
import com.tiagohs.cinema_history.helpers.utils.ColorUtils
import com.tiagohs.cinema_history.helpers.utils.DateUtils
import com.tiagohs.cinema_history.models.dto.MovieFilmographyDTO
import kotlinx.android.synthetic.main.adapter_movie_special_item.view.*
import kotlinx.android.synthetic.main.view_line_five_colors.view.*
import kotlin.math.abs

class MovieItemSpecialAdapter(
    val context: Context?,
    val movies: List<MovieFilmographyDTO>
): RecyclerView.Adapter<MovieItemSpecialAdapter.MovieItemViewHolder>() {

    init {
        setHasStableIds(true)
    }

    var onMovieClicked: ((movieId: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_special_item, parent, false)

        return MovieItemViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        val movie = movies[position]

        holder.bind(movie)
    }

    override fun getItemId(position: Int): Long = movies[position].id?.toLong() ?: position.toLong()

    inner class MovieItemViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private var movie: MovieFilmographyDTO? = null
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

                itemView.movieName.translationX = translationX
                itemView.character.translationX = translationX
                itemView.posterCard.translationX = translationX
            }

        fun bind(movie: MovieFilmographyDTO) {
            this.movie = movie

            val colorAsset = ColorUtils.getRandomColorAssets()

            itemView.overview.text = movie.overview

            bindCharacters(movie.character)
            bindDepartaments(movie.departments, colorAsset)

            itemView.moviePoster.loadImage(movie.posterPath?.imageUrlFromTMDB(ImageSize.POSTER_342))
            itemView.movieBackdrop.loadImage(movie.backdrop?.imageUrlFromTMDB(ImageSize.BACKDROP_300))

            bindTitle(movie)
            bindColor(colorAsset)
        }

        private fun bindTitle(movie: MovieFilmographyDTO) {

            if (movie.releaseDate != null) {
                val year = DateUtils.getYearByDate(movie.releaseDate)

                itemView.movieName.text = if (year > 0)
                    "${movie.title} (${year})"
                else
                    movie.title
            }

        }

        private fun bindColor(colorAsset: ColorAsset) {
            val context = context ?: return
            val backgroundColor = context.getResourceColor("md_${colorAsset.colorName}_500")
            val textColor = context.getResourceColor(colorAsset.textColorName)

            itemView.itemContainerCard.setCardBackgroundColor(backgroundColor)

            itemView.movieName.setTextColor(textColor)
            itemView.character.setTextColor(textColor)
            itemView.overview.setTextColor(textColor)

            itemView.color1.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_500"))
            itemView.color2.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_600"))
            itemView.color3.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_700"))
            itemView.color4.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_800"))
            itemView.color5.setBackgroundColor(context.getResourceColor("md_${colorAsset.colorName}_900"))
        }

        private fun bindCharacters(characters: String?) {

            if (!characters.isNullOrBlank()) {
                itemView.character.visibility = View.VISIBLE
                itemView.character.text = "as $characters"
            } else {
                itemView.character.visibility = View.GONE
            }
        }

        private fun bindDepartaments(departaments: String?, colorAsset: ColorAsset) {

            if (!departaments.isNullOrBlank()) {
                val context = context ?: return
                val listOfDepartments = departaments.split(",").map { it.trim() }.filter { it.isNotBlank() }
                val textColor = context.getResourceColor(colorAsset.textColorName)

                itemView.departments.visibility = View.VISIBLE

                itemView.departments.adapter = DepartamentAdapter(context, listOfDepartments, textColor)
                itemView.departments.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                itemView.departments.addItemDecoration(SpaceOffsetDecoration(10.convertIntToDp(context), SpaceOffsetDecoration.LEFT))
            } else {
                itemView.departments.visibility = View.GONE
            }
        }

        override fun onClick(v: View?) {
            val movieId = movie?.id ?: return

            onMovieClicked?.invoke(movieId)
        }
    }
}

fun RecyclerView.setupParallaxScrollListener() {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = layoutManager as? LinearLayoutManager ?: return

            val scrollOffset = recyclerView.computeHorizontalScrollOffset()
            val offsetFactor = (scrollOffset % measuredWidth) / measuredWidth.toFloat()

            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            findViewHolderForAdapterPosition(firstVisibleItemPosition)?.let {
                (it as? MovieItemSpecialAdapter.MovieItemViewHolder)?.offset = -offsetFactor
            }

            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            if (firstVisibleItemPosition != lastVisibleItemPosition) {
                findViewHolderForAdapterPosition(lastVisibleItemPosition)?.let {
                    (it as? MovieItemSpecialAdapter.MovieItemViewHolder)?.offset = 1 - offsetFactor
                }
            }
        }
    })
}