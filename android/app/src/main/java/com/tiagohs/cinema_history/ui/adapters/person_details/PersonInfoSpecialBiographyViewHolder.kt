package com.tiagohs.cinema_history.ui.adapters.person_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
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
import kotlinx.android.synthetic.main.view_person_department.view.*

class PersonInfoSpecialBiographyViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindPersonInfo(person: Person) {

        itemView.personBiography.text = person.biography

        bindAwards(person)
        bindBirthdayInfo(person)
        bindPersonDepartments(person)
        bindSocial(person)
    }

    private fun bindBirthdayInfo(person: Person) {

        if (person.birthdayFormated.isNotBlank()) {
            itemView.personBirthInfo.visibility = View.VISIBLE
            itemView.personBirthInfo.text = person.birthdayFormated
        }
    }

    private fun bindAwards(person: Person) {

        person.extraInfo?.awards?.let {
            itemView.awardsContainer.visibility = View.VISIBLE
            itemView.awards.text = it

            itemView.awardsContainer.setOnClickListener { context?.openLink("https://www.imdb.com/name/${person.externalIds?.imdbId}/awards") }
        }
    }

    private fun bindSocial(person: Person) {
        setupExternalLinkItem(person.externalIds?.facebookId, itemView.facebookContainer, itemView.facebookContainerClickable, "https://www.facebook.com")
        setupExternalLinkItem(person.externalIds?.twitterId, itemView.twitterContainer, itemView.twitterContainerClickable, "https://twitter.com")
        setupExternalLinkItem(person.externalIds?.instagramId, itemView.instagramContainer, itemView.instagramContainerClickable, "https://instagram.com")
        setupExternalLinkItem(person.externalIds?.imdbId, itemView.imdbContainer, itemView.imdbContainerClickable, "https://www.imdb.com/name")
    }


    private fun bindPersonDepartments(person: Person) {

        person.departmentsList.forEach {
            itemView.jobsScrollView.visibility = View.VISIBLE

            val view = LayoutInflater.from(context).inflate(R.layout.view_person_department, null, false)
            val layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.WRAP_CONTENT, Constraints.LayoutParams.WRAP_CONTENT)

            layoutParams.setMargins(0, 0, 10.convertIntToDp(context), 0)
            view.jobName.text = it

            view.layoutParams = layoutParams
            itemView.jobsContainer.addView(view)
        }
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