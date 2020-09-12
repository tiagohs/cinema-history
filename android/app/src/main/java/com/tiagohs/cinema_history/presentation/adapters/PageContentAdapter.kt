package com.tiagohs.cinema_history.presentation.adapters

import android.content.Intent
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.page.*
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.enums.ContentType

class PageContentAdapter(
    list: List<Content>,
    private val appLanguage: String
) : BaseAdapter<Content, BasePageViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    var presentScreen: ((Intent) -> Unit)? = null
    var onMovieClicked: ((movieId: Int) -> Unit)? = null
    var onPersonClicked: ((personId: Int) -> Unit)? = null

    private var viewHolders: ArrayList<BasePageViewHolder> = ArrayList()

    override fun getLayoutResId(viewType: Int): Int = when (viewType) {
        ContentType.TEXT.ordinal -> TextViewHolder.LAYOUT_ID
        ContentType.BLOCK_SPECIAL.ordinal -> BlockSpecialViewHolder.LAYOUT_ID
        ContentType.GIF.ordinal -> GifViewHolder.LAYOUT_ID
        ContentType.AUDIO_STREAM.ordinal -> AudioStreamViewHolder.LAYOUT_ID
        ContentType.IMAGE.ordinal -> ImageViewHolder.LAYOUT_ID
        ContentType.QUOTE.ordinal -> QuoteViewHolder.LAYOUT_ID
        ContentType.SLIDE.ordinal -> SlideViewHolder.LAYOUT_ID
        ContentType.VIDEO.ordinal -> VideoViewHolder.LAYOUT_ID
        ContentType.LINK_SCREEN.ordinal -> LinkScreenViewHolder.LAYOUT_ID
        ContentType.MOVIE_LIST.ordinal -> MovieListViewHolder.LAYOUT_ID
        ContentType.PERSON_LIST.ordinal -> PersonListViewHolder.LAYOUT_ID
        ContentType.MOVIE_LIST_SPECIAL.ordinal -> MovieListSpecialViewHolder.LAYOUT_ID
        else -> R.layout.adapter_empty
    }

    override fun onCreateViewHolder(viewType: Int, view: View): BasePageViewHolder =
        when (viewType) {
            ContentType.TEXT.ordinal -> TextViewHolder(view)
            ContentType.BLOCK_SPECIAL.ordinal -> BlockSpecialViewHolder(view, presentScreen)
            ContentType.GIF.ordinal -> GifViewHolder(view)
            ContentType.AUDIO_STREAM.ordinal -> AudioStreamViewHolder(view)
            ContentType.IMAGE.ordinal -> ImageViewHolder(view)
            ContentType.QUOTE.ordinal -> QuoteViewHolder(view)
            ContentType.SLIDE.ordinal -> SlideViewHolder(view)
            ContentType.VIDEO.ordinal -> VideoViewHolder(view)
            ContentType.LINK_SCREEN.ordinal -> LinkScreenViewHolder(view, presentScreen)
            ContentType.MOVIE_LIST.ordinal -> MovieListViewHolder(view, appLanguage, onMovieClicked)
            ContentType.PERSON_LIST.ordinal -> PersonListViewHolder(view, onPersonClicked)
            ContentType.MOVIE_LIST_SPECIAL.ordinal -> MovieListSpecialViewHolder(view, onMovieClicked)
            else -> object : BasePageViewHolder(view) {}
        }

    override fun getItemViewType(position: Int): Int = list.getOrNull(position)?.type?.ordinal ?: 0

    override fun getItemId(position: Int): Long = position.toLong()

    fun onDestroy() {

        viewHolders.forEach {
            if (it is GifViewHolder) {
                it.onDestroy()
            }
        }
    }

}