package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.NomineeType
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

            image.loadImage(item.imagePath?.imageUrlFromTMDB(
                ImageSize.PROFILE_185
            ))
            nomineesTitle.setResourceText(item.name)

            setupNomineeStyle(item)
            setupNomineeType(item)
        }

        private fun setupNomineeType(item: Nominee) {
            when (item.type) {
                NomineeType.MOVIE -> setupMovieItem(item)
                NomineeType.PERSON -> setupPersonItem(item)
            }
        }

        private fun setupMovieItem(nomineeMovie: Nominee) {
            containerMovie.hide()
            degradeTop.hide()

            val director = nomineeMovie.director
            if (director != null) {
                nomineesSubtitle.show()
                nomineesSubtitle.setResourceText(director)
                return
            }

            nomineesSubtitle.hide()
        }

        private fun setupPersonItem(nomineePerson: Nominee) {
            setupPersonSubtitle(nomineePerson)
            setupPersonMovie(nomineePerson)
        }

        private fun setupPersonSubtitle(nomineePerson: Nominee) {
            val department = nomineePerson.department
            if (department != null) {
                nomineesSubtitle.setResourceText(department)
                nomineesSubtitle.show()
                return
            }

            nomineesSubtitle.hide()
        }

        private fun setupPersonMovie(nomineePerson: Nominee) {
            val movieNominee = nomineePerson.movie
            if (movieNominee != null) {
                movieImage.loadImage(movieNominee.imagePath?.imageUrlFromTMDB(ImageSize.PROFILE_185))
                movieName.setResourceText(movieNominee.name)
                movieDirector.setResourceText(movieNominee.director)
                degradeTop.show()
                containerMovie.show()
                containerMovie.setOnClickListener { onNomineeClicked?.invoke(movieNominee) }
                return
            }

            degradeTop.hide()
            containerMovie.hide()
            containerMovie.setOnClickListener(null)
        }

        private fun setupNomineeStyle(item: Nominee) {
            val winner = item.winner
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