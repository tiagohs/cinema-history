package com.tiagohs.cinema_history.presentation.adapters

import android.content.Intent
import android.view.ViewGroup
import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.AdapterPageTextBinding
import com.tiagohs.cinema_history.databinding.AdapterPageVideoBinding
import com.tiagohs.cinema_history.databinding.AdapterPageTwitterBinding
import com.tiagohs.cinema_history.databinding.AdapterPageSlideBinding
import com.tiagohs.cinema_history.databinding.AdapterPageRecomendationBinding
import com.tiagohs.cinema_history.databinding.AdapterPageQuoteBinding
import com.tiagohs.cinema_history.databinding.AdapterPagePersonListBinding
import com.tiagohs.cinema_history.databinding.AdapterPageMovieListSpecialBinding
import com.tiagohs.cinema_history.databinding.AdapterPageListMoviesBinding
import com.tiagohs.cinema_history.databinding.AdapterPageLinkScreenBinding
import com.tiagohs.cinema_history.databinding.AdapterPageImgBinding
import com.tiagohs.cinema_history.databinding.AdapterPageGifBinding
import com.tiagohs.cinema_history.databinding.AdapterPageEssayBinding
import com.tiagohs.cinema_history.databinding.AdapterPageBlockSpecialBinding
import com.tiagohs.cinema_history.databinding.AdapterPageAwardNomineesBinding
import com.tiagohs.cinema_history.databinding.AdapterPageAudioStreamBinding
import com.tiagohs.cinema_history.databinding.AdapterEmptyBinding
import com.tiagohs.cinema_history.presentation.adapters.config.BaseAdapter
import com.tiagohs.cinema_history.presentation.adapters.page.*
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.enums.ContentType
import com.tiagohs.entities.main_topics.MainTopicItem

class PageContentAdapter(
    list: List<Content>,
    private val mainTopic: MainTopicItem?,
    private val appLanguage: String
) : BaseAdapter<Content, BasePageViewHolder>(list) {

    init {
        setHasStableIds(true)
    }

    var presentScreen: ((Intent) -> Unit)? = null
    var onMovieClicked: ((movieId: Int) -> Unit)? = null
    var onPersonClicked: ((personId: Int) -> Unit)? = null
    var onNomineeClicked: ((nominee: Nominee) -> Unit)? = null
    var onLinkClicked: ((url: String) -> Unit)? = null

    private var viewHolders: ArrayList<BasePageViewHolder> = ArrayList()

    override fun onCreateViewHolder(viewType: Int, inflater: LayoutInflater, parent: ViewGroup): BasePageViewHolder =
        when (viewType) {
            ContentType.TEXT.ordinal -> TextViewHolder(AdapterPageTextBinding.inflate(inflater, parent, false))
            ContentType.BLOCK_SPECIAL.ordinal -> BlockSpecialViewHolder(AdapterPageBlockSpecialBinding.inflate(inflater, parent, false), presentScreen)
            ContentType.GIF.ordinal -> GifViewHolder(AdapterPageGifBinding.inflate(inflater, parent, false))
            ContentType.AUDIO_STREAM.ordinal -> AudioStreamViewHolder(AdapterPageAudioStreamBinding.inflate(inflater, parent, false))
            ContentType.IMAGE.ordinal -> ImageViewHolder(AdapterPageImgBinding.inflate(inflater, parent, false))
            ContentType.QUOTE.ordinal -> QuoteViewHolder(AdapterPageQuoteBinding.inflate(inflater, parent, false))
            ContentType.SLIDE.ordinal -> SlideViewHolder(AdapterPageSlideBinding.inflate(inflater, parent, false))
            ContentType.VIDEO.ordinal -> VideoViewHolder(AdapterPageVideoBinding.inflate(inflater, parent, false))
            ContentType.RECOMENDATIONS.ordinal -> RecomendationsViewHolder(AdapterPageRecomendationBinding.inflate(inflater, parent, false))
            ContentType.LINK_SCREEN.ordinal -> LinkScreenViewHolder(AdapterPageLinkScreenBinding.inflate(inflater, parent, false), presentScreen)
            ContentType.MOVIE_LIST.ordinal -> MovieListViewHolder(AdapterPageListMoviesBinding.inflate(inflater, parent, false), mainTopic, appLanguage, onMovieClicked)
            ContentType.PERSON_LIST.ordinal -> PersonListViewHolder(AdapterPagePersonListBinding.inflate(inflater, parent, false), onPersonClicked)
            ContentType.MOVIE_LIST_SPECIAL.ordinal -> MovieListSpecialViewHolder(AdapterPageMovieListSpecialBinding.inflate(inflater, parent, false), onMovieClicked)
            ContentType.AWARDS_NOMINEES.ordinal -> AwardsNomineesViewHolder(AdapterPageAwardNomineesBinding.inflate(inflater, parent, false), onNomineeClicked)
            ContentType.TWITTER.ordinal -> TwitterViewHolder(AdapterPageTwitterBinding.inflate(inflater, parent, false))
            ContentType.ESSAY.ordinal -> EssayViewHolder(AdapterPageEssayBinding.inflate(inflater, parent, false), appLanguage, onMovieClicked, onPersonClicked, onLinkClicked)
            else -> object : BasePageViewHolder(AdapterEmptyBinding.inflate(inflater, parent, false)) {}
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