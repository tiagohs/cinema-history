package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterReviewBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.tmdb.movie.Review
import com.tiagohs.helpers.extensions.*
import com.tiagohs.helpers.utils.ContentLanguage
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(
    list: List<Review>,
    val languageName: String?,
    var onExtenalLink: ((String?) -> Unit)?
) : BaseAdapter<Review, ReviewAdapter.ReviewViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): ReviewViewHolder =
        ReviewViewHolder(AdapterReviewBinding.inflate(inflater, parent, false))

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    /** "2015-05-03" -> data longa no idioma do conteúdo ("3 de maio de 2015", "May 3, 2015"...). */
    private fun formatDate(iso: String?): String? {
        if (iso.isNullOrBlank()) return null
        return try {
            val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(iso) ?: return null
            DateFormat.getDateInstance(DateFormat.LONG, Locale(ContentLanguage.current())).format(parsed)
        } catch (e: Exception) {
            null
        }
    }

    inner class ReviewViewHolder(private val binding: AdapterReviewBinding) : BaseViewHolder<Review>(binding) {

        override fun bind(item: Review, position: Int) {
            super.bind(item, position)

            binding.reviewAuthorName.setResourceText(item.reviewerName ?: item.reviewerSiteName)

            val date = formatDate(item.date) ?: item.dateFormated
            val site = item.reviewerSiteName ?: ""
            binding.reviewDetails.setResourceText(
                if (date != null) itemView.context.getString(R.string.review_details_site_date, site, date) else site
            )

            binding.reviewDescription.setResourceText(item.reviewDescription)

            val rating = item.reviewRating
            if (rating != null && rating > 0f) {
                binding.reviewRatingBar.visibility = View.VISIBLE
                binding.reviewRatingBar.rating = rating
            } else {
                binding.reviewRatingBar.visibility = View.GONE
            }

            binding.mediaContainer2.setOnClickListener { onExtenalLink?.invoke(item.reviewUrl) }
            binding.reviewDescription.setOnClickListener { onExtenalLink?.invoke(item.reviewUrl) }

            val logo = item.reviewer?.logo
            if (logo != null) {
                binding.reviewImage.setImageDrawable(itemView.context.getDrawable(logo))
                return
            }

            binding.reviewImage.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_placeholder_review))

            val paddingValue = 10.convertIntToDp(itemView.context)
            binding.reviewImage.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        }
    }
}