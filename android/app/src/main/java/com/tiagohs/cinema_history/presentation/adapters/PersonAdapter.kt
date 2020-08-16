package com.tiagohs.cinema_history.presentation.adapters

import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceText
import kotlinx.android.synthetic.main.adapter_person.view.*

class PersonAdapter(
    list: List<PersonDTO>,
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

            itemView.personName.setResourceText(item.name)
            itemView.personSubtitle.setResourceText(item.subtitle)

            itemView.personImage.loadImage(
                item.imagePath?.imageUrlFromTMDB(ImageSize.PROFILE_185),
                R.drawable.placeholder_movie_person,
                R.drawable.placeholder_movie_person
            )
        }

        override fun onClick(v: View?) {
            val personId = item?.id ?: return

            onPersonClicked?.invoke(personId)
        }
    }

}