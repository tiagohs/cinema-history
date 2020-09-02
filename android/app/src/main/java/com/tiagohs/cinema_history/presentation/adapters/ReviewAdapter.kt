package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.tmdb.movie.Review
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.adapter_review.*

class ReviewAdapter(
    list: List<Review>,
    val countryName: String?,
    var onExtenalLink: ((String?) -> Unit)?
) : BaseAdapter<Review, ReviewAdapter.ReviewViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_review

    override fun onCreateViewHolder(viewType: Int, view: View): ReviewViewHolder =
        ReviewViewHolder(view)

    override fun getItemId(position: Int): Long = list[position].hashCode().toLong()

    inner class ReviewViewHolder(view: View) : BaseViewHolder<Review>(view) {

        override fun bind(item: Review, position: Int) {
            super.bind(item, position)

            reviewAuthorName.setResourceText(item.reviewerName)
            reviewDetails.setResourceText(containerView.context.getString(R.string.review_details_format, item.dateFormated, item.reviewerSiteName, countryName))
            reviewDescription.setResourceText(item.reviewDescription)

            reviewRatingBar.rating = item.reviewRating

            mediaContainer2.setOnClickListener { onExtenalLink?.invoke(item.reviewUrl) }
            reviewDescription.setOnClickListener { onExtenalLink?.invoke(item.reviewUrl) }

            val logo = item.reviewer?.logo
            if (logo != null) {
                reviewImage.setImageDrawable(containerView.context.getDrawable(logo))
                return
            }

            reviewImage.setImageDrawable(containerView.context.getDrawable(R.drawable.ic_placeholder_review))

            val paddingValue = 10.convertIntToDp(containerView.context)
            reviewImage.setPadding(paddingValue, paddingValue, paddingValue, paddingValue)
        }
    }
}