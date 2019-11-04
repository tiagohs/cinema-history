package com.tiagohs.cinema_history.ui.adapters.person_details

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.openLink
import com.tiagohs.cinema_history.models.tmdb.person.Person
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.*
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.facebookContainer
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.facebookContainerClickable
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.imdbContainer
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.imdbContainerClickable
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.instagramContainer
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.instagramContainerClickable
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.twitterContainer
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.twitterContainerClickable

class PersonInfoSpecialBiographyViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindPersonInfo(person: Person) {

        itemView.personBiography.text = person.biography

        bindAwards(person)
        bindBirthdayInfo(person)
        bindSocial(person)
    }

    private fun bindBirthdayInfo(person: Person) {

        if (person.birthdayFormated.isNotBlank()) {
            itemView.personBirthInfo.visibility = View.VISIBLE
            itemView.personBirthInfo.text = person.birthdayFormated
        }
    }

    private fun bindAwards(person: Person) {

        person.extraInfo?.let {
            itemView.awards.text = it.awards
        }
    }

    private fun bindSocial(person: Person) {
        setupExternalLinkItem(person.externalIds?.facebookId, itemView.facebookContainer, itemView.facebookContainerClickable, "https://www.facebook.com")
        setupExternalLinkItem(person.externalIds?.twitterId, itemView.twitterContainer, itemView.twitterContainerClickable, "https://twitter.com")
        setupExternalLinkItem(person.externalIds?.instagramId, itemView.instagramContainer, itemView.instagramContainerClickable, "https://instagram.com")
        setupExternalLinkItem(person.externalIds?.imdbId, itemView.imdbContainer, itemView.imdbContainerClickable, "https://www.imdb.com/name")
    }

    private fun setupExternalLinkItem(externalLinkId: String?, container: View, containerClickable: View, baseUrl: String) {
        val context = context ?: return
        val externalLinkID = externalLinkId ?: return

        if (externalLinkID.isNotBlank()) {
            val externalLink = if (baseUrl.isNotBlank()) "$baseUrl/$externalLinkID" else externalLinkID

            container.visibility = View.VISIBLE
            containerClickable.setOnClickListener { context.openLink(externalLink) }
        }

    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_special_biography
    }
}