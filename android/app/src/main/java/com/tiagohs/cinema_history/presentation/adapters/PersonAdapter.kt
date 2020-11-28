package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.adapter_person.*

class PersonAdapter(
    list: List<PersonDTO>,
    private val isSpecial: Boolean = false,
    private val onPersonClicked: ((personId: Int) -> Unit)?
) : BaseAdapter<PersonDTO, PersonAdapter.PersonViewHolder>(list) {

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_person

    override fun onCreateViewHolder(viewType: Int, view: View): PersonViewHolder =
        PersonViewHolder(view)

    inner class PersonViewHolder(view: View) : BaseViewHolder<PersonDTO>(view),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun bind(item: PersonDTO, position: Int) {
            super.bind(item, position)

            personName.setResourceText(item.name)
            personImage.loadImage(
                item.imagePath?.imageUrlFromTMDB( if (isSpecial) ImageSize.PROFILE_632 else ImageSize.PROFILE_185 ),
                containerView.context.getString(R.string.person_photo_description, item.name),
                R.drawable.placeholder_movie_person,
                R.drawable.placeholder_movie_person
            )

            setupSubtitle(item)

            if (isSpecial) {
                val width = containerView.context.getDimen(R.dimen.person_item_width_special)
                val height = containerView.context.getDimen(R.dimen.person_item_height_special)

                setupItem(width, height, 5.convertIntToDp(containerView.context), R.dimen.text_size_zeplin_28pt)
                return
            }

            val width = containerView.context.getDimen(R.dimen.person_item_width)
            val height = containerView.context.getDimen(R.dimen.person_item_height)

            setupItem(width, height, 10.convertIntToDp(containerView.context), R.dimen.text_size_zeplin_22pt)
        }

        private fun setupItem(width: Float, height: Float, margins: Int, textSize: Int) {
            personImageContainer.layoutParams = ConstraintLayout.LayoutParams(width.toInt(), height.toInt()).apply {
                topToTop = ConstraintSet.PARENT_ID
                bottomToBottom = ConstraintSet.PARENT_ID
                startToStart = ConstraintSet.PARENT_ID
                endToEnd = ConstraintSet.PARENT_ID

                setMargins(margins, 0, margins, 10.convertIntToDp(containerView.context))
            }

            personName.setResourceTextSize(textSize)
        }

        private fun setupSubtitle(item: PersonDTO) {
            val subtitle = item.subtitle
            if (subtitle != null) {
                personSubtitle.show()
                personSubtitle.setResourceText(subtitle)
                return
            }

            personSubtitle.hide()
        }

        override fun onClick(v: View?) {
            val personId = item?.id ?: return

            onPersonClicked?.invoke(personId)
        }
    }

}