package com.tiagohs.cinema_history.presentation.adapters

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.Glossary
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.helpers.extensions.setResourceBackgroundColor
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.utils.ColorUtils
import kotlinx.android.synthetic.main.adapter_glossary.*

class GlossaryAdapter(
    list: List<Glossary>,
    private val mainTopic: MainTopicItem?,
    private val appLanguage: String
) : BaseAdapter<Glossary, GlossaryAdapter.GlossaryViewHolder>(list) {

    var presentScreen: ((Intent) -> Unit)? = null
    var onMovieClicked: ((movieId: Int) -> Unit)? = null
    var onPersonClicked: ((personId: Int) -> Unit)? = null
    var onLinkClicked: ((url: String) -> Unit)? = null

    override fun getLayoutResId(viewType: Int): Int = R.layout.adapter_glossary

    override fun onCreateViewHolder(viewType: Int, view: View): GlossaryViewHolder =
        GlossaryViewHolder(view)

    inner class GlossaryViewHolder(view: View) : BaseViewHolder<Glossary>(view) {

        override fun bind(item: Glossary, position: Int) {
            super.bind(item, position)

            val colorAsset = ColorUtils.getRandomColorAssets()

            separator.setResourceBackgroundColor("md_${colorAsset.colorName}_500")
            contentTitle.setResourceText(item.name)
            contentList.apply {
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