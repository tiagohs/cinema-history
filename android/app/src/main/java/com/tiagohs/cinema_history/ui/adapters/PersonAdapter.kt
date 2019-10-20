package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageSize
import com.tiagohs.cinema_history.helpers.extensions.imageUrlFromTMDB
import com.tiagohs.cinema_history.models.PersonDTO
import kotlinx.android.synthetic.main.adapter_person.view.*

class PersonAdapter(
    val context: Context?,
    val persons: List<PersonDTO>
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

    inner class PersonViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bind(person: PersonDTO) {
            itemView.personName.text = person.name
            itemView.personSubtitle.text = person.subtitle

            Picasso.get()
                .load(person.imagePath?.imageUrlFromTMDB(ImageSize.PROFILE_185))
                .into(itemView.personImage)
        }
    }
}