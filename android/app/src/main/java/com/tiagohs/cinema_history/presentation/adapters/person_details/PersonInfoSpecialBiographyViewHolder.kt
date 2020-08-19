package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.openLink
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.show
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.*
import kotlinx.android.synthetic.main.adapter_person_info_special_biography.view.*
import kotlinx.android.synthetic.main.view_person_department.view.*

class PersonInfoSpecialBiographyViewHolder(
    view: View,
    private var onLinkClick: ((String?) -> Unit)? = null
) : BaseViewHolder<PersonInfo>(view) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val person = item.person

        personBiography.setResourceText(person.biography)

        bindAwards(person)
        bindBirthdayInfo(person)
        bindPersonDepartments(person)
        bindSocial(person)
    }

    private fun bindBirthdayInfo(person: Person) {

        if (person.birthdayFormated.isNotBlank()) {
            personBirthInfo.show()
            personBirthInfo.setResourceText(person.birthdayFormated)
        }
    }

    private fun bindAwards(person: Person) {
        val context = containerView.context ?: return

        person.extraInfo?.awards?.let {
            awardsContainer.show()
            awards.setResourceText(it)

            awardsContainer.setOnClickListener {
                onLinkClick?.invoke(
                    context.getString(
                        R.string.imdb_awards_link,
                        person.externalIds?.imdbId
                    )
                )
            }
        }
    }

    private fun bindSocial(person: Person) {
        setupExternalLinkItem(
            person.externalIds?.facebookId,
            facebookContainer,
            facebookContainerClickable,
            R.string.facebook_link
        )
        setupExternalLinkItem(
            person.externalIds?.twitterId,
            twitterContainer,
            twitterContainerClickable,
            R.string.twitter_link
        )
        setupExternalLinkItem(
            person.externalIds?.instagramId,
            instagramContainer,
            instagramContainerClickable,
            R.string.instagram_link
        )
        setupExternalLinkItem(
            person.externalIds?.imdbId,
            imdbContainer,
            imdbContainerClickable,
            R.string.imdb_person_link
        )
    }

    private fun bindPersonDepartments(person: Person) {
        val context = containerView.context ?: return

        person.departmentsList.forEach {
            jobsScrollView.show()

            val view =
                LayoutInflater.from(context).inflate(R.layout.view_person_department, null, false)
            val layoutParams = Constraints.LayoutParams(
                Constraints.LayoutParams.WRAP_CONTENT,
                Constraints.LayoutParams.WRAP_CONTENT
            )

            layoutParams.setMargins(0, 0, 10.convertIntToDp(context), 0)
            view.jobName.setResourceText(it)

            view.layoutParams = layoutParams
            jobsContainer.addView(view)
        }
    }

    private fun setupExternalLinkItem(
        externalLinkId: String?,
        container: View,
        containerClickable: View,
        baseUrl: Int
    ) {
        val context = containerView.context ?: return
        val externalLinkID = externalLinkId ?: return

        if (externalLinkID.isNotBlank()) {
            val externalLink =
                if (baseUrl == 0) externalLinkID else context.getString(baseUrl, externalLinkID)

            container.show()
            containerClickable.setOnClickListener { onLinkClick?.invoke(externalLink) }
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_person_info_special_biography
    }
}