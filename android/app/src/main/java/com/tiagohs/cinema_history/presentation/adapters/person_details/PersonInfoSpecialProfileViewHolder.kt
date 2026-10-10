package com.tiagohs.cinema_history.presentation.adapters.person_details

import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ViewPersonProfileItemBinding
import com.tiagohs.cinema_history.databinding.ViewPersonProfileMediaBinding
import com.tiagohs.cinema_history.databinding.AdapterPersonInfoSpecialProfileBinding
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.person_info.PersonInfo
import com.tiagohs.entities.tmdb.person.PersonProfileMedia
import com.tiagohs.helpers.extensions.hide
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.setResourceText
import com.tiagohs.helpers.extensions.show

class PersonInfoSpecialProfileViewHolder(
    private val binding: AdapterPersonInfoSpecialProfileBinding,
    private val onVideoClick: ((String?) -> Unit)? = null
) : BaseViewHolder<PersonInfo>(binding) {

    override fun bind(item: PersonInfo, position: Int) {
        super.bind(item, position)
        val context = itemView.context ?: return
        val person = item.person
        val personProfile = person.extraInfo?.profile ?: return
        val inflater = LayoutInflater.from(context)

        binding.personProfileContainer.removeAllViews()

        personProfile.forEach {
            val itemBinding = ViewPersonProfileItemBinding.inflate(inflater, null, false)
            itemBinding.root.layoutParams = Constraints.LayoutParams(
                Constraints.LayoutParams.MATCH_PARENT,
                Constraints.LayoutParams.WRAP_CONTENT
            )

            itemBinding.profileContent.setupLinkableTextView(context)
            itemBinding.profileItemTitle.setResourceText(it.years)
            itemBinding.profileContent.setResourceStyledText(it.content)

            binding.personProfileContainer.addView(itemBinding.root)

            it.media?.let { media -> addMedia(inflater, media) }
        }
    }

    /** Imagem, vídeo ou citação logo depois do parágrafo, dentro da mesma linha do tempo. */
    private fun addMedia(inflater: LayoutInflater, media: PersonProfileMedia) {
        if (!media.isImage && !media.isVideo && !media.isQuote) return

        val context = itemView.context
        val mediaBinding = ViewPersonProfileMediaBinding.inflate(inflater, null, false)
        mediaBinding.root.layoutParams = Constraints.LayoutParams(
            Constraints.LayoutParams.MATCH_PARENT,
            Constraints.LayoutParams.WRAP_CONTENT
        )

        when {
            media.isQuote -> {
                mediaBinding.mediaQuoteContainer.show()
                mediaBinding.mediaQuote.text = context.getString(R.string.person_profile_quote_format, media.quote)
                mediaBinding.mediaQuoteAuthor.setResourceText(media.author)
            }
            else -> {
                mediaBinding.mediaCard.show()

                if (media.isVideo) {
                    val videoId = media.videoId?.trim()
                    mediaBinding.mediaImage.loadImage(
                        context.getString(R.string.youtube_thumbnail_hq_link, videoId),
                        media.title,
                        placeholder = null
                    )
                    mediaBinding.mediaVideoScrim.show()
                    mediaBinding.mediaPlay.show()
                    mediaBinding.mediaCard.isClickable = true
                    mediaBinding.mediaCard.isFocusable = true
                    mediaBinding.mediaCard.contentDescription = context.getString(R.string.person_profile_play_video, media.title)
                    mediaBinding.mediaCard.setOnClickListener { onVideoClick?.invoke(videoId) }
                } else {
                    media.image?.let { mediaBinding.mediaImage.loadImage(it, placeholder = null) }
                    mediaBinding.mediaImage.contentDescription = media.image?.contentDescription ?: media.title
                    mediaBinding.mediaVideoScrim.hide()
                    mediaBinding.mediaPlay.hide()
                }

                bindText(mediaBinding, media)
            }
        }

        binding.personProfileContainer.addView(mediaBinding.root)
    }

    private fun bindText(mediaBinding: ViewPersonProfileMediaBinding, media: PersonProfileMedia) {
        val context = itemView.context

        if (!media.title.isNullOrBlank()) {
            mediaBinding.mediaTitle.show()
            mediaBinding.mediaTitle.text = media.title
        }
        if (!media.text.isNullOrBlank()) {
            mediaBinding.mediaText.show()
            mediaBinding.mediaText.setupLinkableTextView(context)
            mediaBinding.mediaText.setResourceStyledText(media.text)
        }
        if (!media.source.isNullOrBlank()) {
            mediaBinding.mediaSource.show()
            mediaBinding.mediaSource.setResourceStyledText(media.source)
        }
    }
}
