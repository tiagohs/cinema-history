package com.tiagohs.cinema_history.presentation.adapters

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

class ReviewAdapter(
    list: List<Review>,
    val countryName: String?,
    var onExtenalLink: ((String?) -> Unit)?
) : BaseAdapter<Review, ReviewAdapter.ReviewViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): ReviewViewHolder =
        ReviewViewHolder(AdapterReviewBinding.inflate(inflater, parent, false))

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    inner class ReviewViewHolder(private val binding: AdapterReviewBinding) : BaseViewHolder<Review>(binding) {

        override fun bind(item: Review, position: Int) {
            super.bind(item, position)

            binding.reviewAuthorName.setResourceText(item.reviewerName)

            if (item.dateFormated != null) {
                binding.reviewDetails.setResourceText(itemView.context.getString(R.string.review_details_format_with_date, item.dateFormated, item.reviewerSiteName, countryName))
            } else {
                binding.reviewDetails.setResourceText(itemView.context.getString(R.string.review_details_format, item.reviewerSiteName, countryName))
            }

            binding.reviewDescription.setResourceText(item.reviewDescription)

            binding.reviewRatingBar.rating = item.reviewRating

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