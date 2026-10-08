package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ViewPersonDepartmentBinding
import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialBiographyBinding
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

class PersonInfoSpecialBiographyViewHolder(
    private val binding: AdapterPersonInfoSpecialBiographyBinding,
    private var onLinkClick: ((String?) -> Unit)? = null
) : BaseViewHolder<PersonInfo>(binding) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val person = item.person

        binding.personBiography.setResourceText(person.biography)

        bindAwards(person)
        bindBirthdayInfo(person)
        bindPersonDepartments(person)
        bindSocial(person)
    }

    private fun bindBirthdayInfo(person: Person) {

        if (person.birthdayFormated.isNotBlank()) {
            binding.personBirthInfo.show()
            binding.personBirthInfo.setResourceText(person.birthdayFormated)
        }
    }

    private fun bindAwards(person: Person) {
        val context = itemView.context ?: return

        person.extraInfo?.awards?.let {
            binding.awardsContainer.show()
            binding.awards.setResourceText(it)

            binding.awardsContainer.setOnClickListener {
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
            binding.facebookContainer,
            binding.facebookContainerClickable,
            R.string.facebook_link
        )
        setupExternalLinkItem(
            person.externalIds?.twitterId,
            binding.twitterContainer,
            binding.twitterContainerClickable,
            R.string.twitter_link
        )
        setupExternalLinkItem(
            person.externalIds?.instagramId,
            binding.instagramContainer,
            binding.instagramContainerClickable,
            R.string.instagram_link
        )
        setupExternalLinkItem(
            person.externalIds?.imdbId,
            binding.imdbContainer,
            binding.imdbContainerClickable,
            R.string.imdb_person_link
        )
    }

    private fun bindPersonDepartments(person: Person) {
        val context = itemView.context ?: return

        person.departmentsList.forEach {
            binding.jobsScrollView.show()

            val itemBinding = ViewPersonDepartmentBinding.inflate(LayoutInflater.from(context), null, false)
            val layoutParams = Constraints.LayoutParams(
                Constraints.LayoutParams.WRAP_CONTENT,
                Constraints.LayoutParams.WRAP_CONTENT
            )

            layoutParams.setMargins(0, 0, 10.convertIntToDp(context), 0)
            itemBinding.jobName.setResourceText(it)

            itemBinding.root.layoutParams = layoutParams
            binding.jobsContainer.addView(itemBinding.root)
        }
    }

    private fun setupExternalLinkItem(
        externalLinkId: String?,
        container: View,
        containerClickable: View,
        baseUrl: Int
    ) {
        val context = itemView.context ?: return
        val externalLinkID = externalLinkId ?: return

        if (externalLinkID.isNotBlank()) {
            val externalLink =
                if (baseUrl == 0) externalLinkID else context.getString(baseUrl, externalLinkID)

            container.show()
            containerClickable.setOnClickListener { onLinkClick?.invoke(externalLink) }
        }
    }
}