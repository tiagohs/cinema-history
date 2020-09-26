package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.adapter_award_nominees_item.*

class NomineeAdapter(
    list: List<Nominee>,
    private val onNomineeClicked: ((nominee: Nominee) -> Unit)?
) : BaseAdapter<Nominee, NomineeAdapter.NomineeViewHolder>(list) {

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_award_nominees_item

    override fun onCreateViewHolder(viewType: Int, view: View): NomineeViewHolder =
        NomineeViewHolder(view)

    inner class NomineeViewHolder(view: View) : BaseViewHolder<Nominee>(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: Nominee, position: Int) {
            super.bind(item, position)

            val winner = item.winner

            image.loadImage(item.imagePath?.imageUrlFromTMDB(
                ImageSize.PROFILE_185
            ))
            nomineesTitle.setResourceText(item.title)
            nomineesSubtitle.setResourceText(item.subtitle)

            if (winner == true) {
                setupWinner()
                return
            }

            setupDefault()
        }

        private fun setupDefault() {
            imageCardContainer.setCardBackgroundColor(containerView.context.getResourceColor(R.color.md_white_1000))
            winnerMessage.hide()
            imageCardContainer.cardElevation = 0f
            imageCardContainer.elevation = 0f
            imageCard.cardElevation = 5.convertIntToDp(containerView.context).toFloat()
            imageCard.elevation = 5.convertIntToDp(containerView.context).toFloat()
        }

        private fun setupWinner() {
            imageCardContainer.setCardBackgroundColor(containerView.context.getResourceColor(R.color.oscar))
            imageCardContainer.cardElevation = 5.convertIntToDp(containerView.context).toFloat()
            imageCardContainer.elevation = 5.convertIntToDp(containerView.context).toFloat()
            imageCard.cardElevation = 0f
            imageCard.elevation = 0f
            winnerMessage.show()
        }

        override fun onClick(v: View?) {
            val item = item ?: return

            onNomineeClicked?.invoke(item)
        }
    }

}