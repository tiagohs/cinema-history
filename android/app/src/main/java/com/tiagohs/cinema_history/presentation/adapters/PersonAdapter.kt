package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterPersonBinding
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.*

class PersonAdapter(
    list: List<PersonDTO>,
    private val isSpecial: Boolean = false,
    private val onPersonClicked: ((personId: Int) -> Unit)?
) : BaseAdapter<PersonDTO, PersonAdapter.PersonViewHolder>(list) {

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): PersonViewHolder =
        PersonViewHolder(AdapterPersonBinding.inflate(inflater, parent, false))

    inner class PersonViewHolder(private val binding: AdapterPersonBinding) : BaseViewHolder<PersonDTO>(binding),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: PersonDTO, position: Int) {
            super.bind(item, position)

            binding.personName.setResourceText(item.name)
            binding.personImage.loadImage(
                item.imagePath?.imageUrlFromTMDB( if (isSpecial) ImageSize.PROFILE_632 else ImageSize.PROFILE_185 ),
                itemView.context.getString(R.string.person_photo_description, item.name),
                R.drawable.placeholder_movie_person,
                R.drawable.placeholder_movie_person
            )

            setupSubtitle(item)

            if (isSpecial) {
                val width = itemView.context.getDimen(R.dimen.person_item_width_special)
                val height = itemView.context.getDimen(R.dimen.person_item_height_special)

                setupItem(width, height, 5.convertIntToDp(itemView.context), R.dimen.text_size_zeplin_28pt)
                return
            }

            val width = itemView.context.getDimen(R.dimen.person_item_width)
            val height = itemView.context.getDimen(R.dimen.person_item_height)

            setupItem(width, height, 10.convertIntToDp(itemView.context), R.dimen.text_size_zeplin_22pt)
        }

        private fun setupItem(width: Float, height: Float, margins: Int, textSize: Int) {
            binding.personImageContainer.layoutParams = ConstraintLayout.LayoutParams(width.toInt(), height.toInt()).apply {
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID

                setMargins(margins, 0, margins, 10.convertIntToDp(itemView.context))
            }

            binding.personName.setResourceTextSize(textSize)
        }

        private fun setupSubtitle(item: PersonDTO) {
            val subtitle = item.subtitle
            if (subtitle != null) {
                binding.personSubtitle.show()
                binding.personSubtitle.setResourceText(subtitle)
                return
            }

            binding.personSubtitle.hide()
        }

        override fun onClick(v: View?) {
            val personId = item?.id ?: return

            onPersonClicked?.invoke(personId)
        }
    }

}