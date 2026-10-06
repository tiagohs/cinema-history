package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterAwardNomineesItemBinding
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.NomineeType
import com.tiagohs.helpers.extensions.*

class NomineeAdapter(
    list: List<Nominee>,
    private val onNomineeClicked: ((nominee: Nominee) -> Unit)?
) : BaseAdapter<Nominee, NomineeAdapter.NomineeViewHolder>(list) {

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): NomineeViewHolder =
        NomineeViewHolder(AdapterAwardNomineesItemBinding.inflate(inflater, parent, false))

    inner class NomineeViewHolder(private val binding: AdapterAwardNomineesItemBinding) : BaseViewHolder<Nominee>(binding),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: Nominee, position: Int) {
            super.bind(item, position)

            binding.image.loadImage(item.imagePath?.imageUrlFromTMDB(
                ImageSize.PROFILE_185
            ))
            binding.nomineesTitle.setResourceText(item.name)

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
            binding.containerMovie.hide()
            binding.degradeTop.hide()

            val director = nomineeMovie.director
            if (director != null) {
                binding.nomineesSubtitle.show()
                binding.nomineesSubtitle.setResourceText(director)
                return
            }

            binding.nomineesSubtitle.hide()
        }

        private fun setupPersonItem(nomineePerson: Nominee) {
            setupPersonSubtitle(nomineePerson)
            setupPersonMovie(nomineePerson)
        }

        private fun setupPersonSubtitle(nomineePerson: Nominee) {
            val department = nomineePerson.department
            if (department != null) {
                binding.nomineesSubtitle.setResourceText(department)
                binding.nomineesSubtitle.show()
                return
            }

            binding.nomineesSubtitle.hide()
        }

        private fun setupPersonMovie(nomineePerson: Nominee) {
            val movieNominee = nomineePerson.movie
            if (movieNominee != null) {
                binding.movieImage.loadImage(movieNominee.imagePath?.imageUrlFromTMDB(ImageSize.PROFILE_185))
                binding.movieName.setResourceText(movieNominee.name)
                binding.movieDirector.setResourceText(movieNominee.director)
                binding.degradeTop.show()
                binding.containerMovie.show()
                binding.containerMovie.setOnClickListener { onNomineeClicked?.invoke(movieNominee) }
                return
            }

            binding.degradeTop.hide()
            binding.containerMovie.hide()
            binding.containerMovie.setOnClickListener(null)
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
            binding.imageCardContainer.setCardBackgroundColor(itemView.context.getResourceColor(R.color.md_white_1000))
            binding.winnerMessage.hide()
            binding.imageCardContainer.cardElevation = 0f
            binding.imageCardContainer.elevation = 0f
            binding.imageCard.cardElevation = 5.convertIntToDp(itemView.context).toFloat()
            binding.imageCard.elevation = 5.convertIntToDp(itemView.context).toFloat()
        }

        private fun setupWinner() {
            binding.imageCardContainer.setCardBackgroundColor(itemView.context.getResourceColor(R.color.oscar))
            binding.imageCardContainer.cardElevation = 5.convertIntToDp(itemView.context).toFloat()
            binding.imageCardContainer.elevation = 5.convertIntToDp(itemView.context).toFloat()
            binding.imageCard.cardElevation = 0f
            binding.imageCard.elevation = 0f
            binding.winnerMessage.show()
        }

        override fun onClick(v: View?) {
            val item = item ?: return

            onNomineeClicked?.invoke(item)
        }
    }

}