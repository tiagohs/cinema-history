package com.tiagohs.cinema_history.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.helpers.extensions.loadImage
import kotlinx.android.synthetic.main.adapter_person.view.*

class PersonAdapter(
    val context: Context?,
    val persons: List<PersonDTO>,
    private val onPersonClicked: ((personId: Int) -> Unit)?
): RecyclerView.Adapter<PersonAdapter.PersonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_person, parent, false)

        return PersonViewHolder(view)
    }

    override fun getItemCount(): Int = persons.size

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = persons[position]

        holder.bind(person)
    }

    inner class PersonViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        var person: PersonDTO? = null

        fun bind(person: PersonDTO) {
            this.person = person

            itemView.personName.text = person.name
            itemView.personSubtitle.text = person.subtitle

            itemView.personImage.loadImage(
                person.imagePath?.imageUrlFromTMDB(ImageSize.PROFILE_185),
                R.drawable.placeholder_movie_person,
                R.drawable.placeholder_movie_person
            )
        }

        override fun onClick(v: View?) {
            val personId = person?.id ?: return

            onPersonClicked?.invoke(personId)
        }
    }
}