package com.tiagohs.cinema_history.presentation.adapters.page

import com.tiagohs.cinema_history.databinding.AdapterPagePersonListBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.PersonAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.ContentPersonList
import com.tiagohs.entities.dto.PersonDTO
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.movie_info.MovieInfoPersonList
import com.tiagohs.helpers.extensions.convertIntToDp
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.tools.SpaceOffsetDecoration

class PersonListViewHolder(
    private val binding: AdapterPagePersonListBinding,
    private val onPersonClicked: ((personId: Int) -> Unit)?
) : BasePageViewHolder(binding) {

    private var isSetup = false

    override fun bind(item: Content, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val contentPersonList = item as? ContentPersonList ?: return

        if (!isSetup) {
            binding.personList.addItemDecoration(
                SpaceOffsetDecoration(
                    8.convertIntToDp(context),
                    SpaceOffsetDecoration.LEFT
                )
            )

            isSetup = true
        }
        val persons = contentPersonList.persons?.map {
            PersonDTO(
                it.id,
                it.profilePath,
                it.name
            )
        } ?: emptyList()

        binding.personList.apply {
            adapter = PersonAdapter(persons, isSpecial = true, onPersonClicked)
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        }

        val contentPersonTitle = contentPersonList.title
        if (contentPersonTitle != null) {
            binding.title.setResourceText(contentPersonTitle)
            return
        }

        binding.title.setResourceText(R.string.should_know)
    }
}