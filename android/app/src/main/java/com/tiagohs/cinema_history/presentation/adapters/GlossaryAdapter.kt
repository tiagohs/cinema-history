package com.tiagohs.cinema_history.presentation.adapters

import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterGlossaryBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.Glossary
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.utils.ColorUtils

class GlossaryAdapter(
    list: List<Glossary>,
    private val mainTopic: MainTopicItem?,
    private val appLanguage: String
) : BaseAdapter<Glossary, GlossaryAdapter.GlossaryViewHolder>(list) {

    var presentScreen: ((Intent) -> Unit)? = null
    var onMovieClicked: ((movieId: Int) -> Unit)? = null
    var onPersonClicked: ((personId: Int) -> Unit)? = null
    var onLinkClicked: ((url: String) -> Unit)? = null

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): GlossaryViewHolder =
        GlossaryViewHolder(AdapterGlossaryBinding.inflate(inflater, parent, false))

    inner class GlossaryViewHolder(private val binding: AdapterGlossaryBinding) : BaseViewHolder<Glossary>(binding) {

        override fun bind(item: Glossary, position: Int) {
            super.bind(item, position)

            val colorAsset = ColorUtils.getRandomColorAssets()

            binding.separator.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
            binding.contentTitle.setResourceText(item.name)
            binding.contentList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = PageContentAdapter(item.contentList, mainTopic, appLanguage).apply {
                    presentScreen = this@GlossaryAdapter.presentScreen
                    onMovieClicked = this@GlossaryAdapter.onMovieClicked
                    onPersonClicked = this@GlossaryAdapter.onPersonClicked
                    onLinkClicked = this@GlossaryAdapter.onLinkClicked
                }
            }
        }
    }
}