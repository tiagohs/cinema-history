package com.tiagohs.cinema_history.presentation.adapters.awards

import android.os.Parcelable
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.AdapterAwardCategoryRowBinding
import com.tiagohs.cinema_history.databinding.AdapterAwardInfoHeaderBinding
import com.tiagohs.cinema_history.databinding.AdapterAwardMediaRowBinding
import com.tiagohs.cinema_history.databinding.AdapterAwardPersonItemBinding
import com.tiagohs.cinema_history.databinding.AdapterAwardReadMoreBinding
import com.tiagohs.cinema_history.databinding.AdapterAwardSectionTitleBinding
import com.tiagohs.cinema_history.databinding.AdapterAwardTextCardBinding
import com.tiagohs.cinema_history.databinding.AdapterAwardVideoItemBinding
import com.tiagohs.cinema_history.databinding.AdapterAwardVideoWideBinding
import com.tiagohs.cinema_history.extensions.setupLinkableTextView
import com.tiagohs.cinema_history.presentation.adapters.NomineeAdapter
import com.tiagohs.cinema_history.presentation.adapters.page.AwardsNomineesViewHolder
import com.tiagohs.cinema_history.presentation.views.Images
import com.tiagohs.cinema_history.presentation.views.PressScale
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.click.Click
import com.tiagohs.entities.contents.ContentVideo
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.SocialType
import com.tiagohs.entities.tmdb.person.Person
import com.tiagohs.helpers.extensions.setResourceStyledText
import com.tiagohs.helpers.extensions.styledString

/** Ações disparadas pelas listas da tela de prêmios. */
interface AwardActions {
    fun onNomineeClicked(nominee: Nominee, sharedView: View)
    fun onPersonClicked(personId: Int, sharedView: View)
    fun onVideoClicked(videoId: String)
    fun onLinkClicked(url: String?)
    fun onBlockClicked(click: Click)
    fun onReadMoreClicked() {}
}

/**
 * Lista vertical da tela de prêmios (aba do ano e aba "Sobre"), no tema escuro.
 *
 * - DiffUtil (ListAdapter) com chaves estáveis por ano.
 * - Ao trocar de ano, as fileiras entram escalonadas (fade + slide), uma única vez por troca.
 * - Fileiras horizontais compartilham um RecycledViewPool.
 */
class AwardContentAdapter(
    private val glide: RequestManager,
    private val actions: AwardActions,
    private val motionEnabled: Boolean
) : ListAdapter<AwardItem, RecyclerView.ViewHolder>(DIFF) {

    /** Pool único para os cartões de indicados de todas as categorias. */
    private val nomineePool = RecyclerView.RecycledViewPool().apply {
        setMaxRecycledViews(NomineeAdapter.TYPE_NOMINEE, 24)
        setMaxRecycledViews(NomineeAdapter.TYPE_WINNER, 8)
    }
    private val mediaPool = RecyclerView.RecycledViewPool()
    private val scrollStates = HashMap<String, Parcelable?>()
    private val expandedKeys = HashSet<String>()

    private var pressScale: PressScale? = null
    private var entranceStart = 0L
    private var entranceIndex = 0

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).key.hashCode().toLong()

    override fun getItemViewType(position: Int): Int = getItem(position).viewType

    /**
     * Troca o conteúdo inteiro (novo ano). As posições horizontais e os textos expandidos do ano
     * anterior são descartados; as fileiras do novo ano entram escalonadas.
     */
    fun submitYear(items: List<AwardItem>, onCommitted: (() -> Unit)? = null) {
        scrollStates.clear()
        expandedKeys.clear()
        submitList(items) {
            playEntrance()
            onCommitted?.invoke()
        }
    }

    /** As próximas linhas ligadas (na ordem em que aparecem) entram com fade + slide. */
    fun playEntrance() {
        if (!motionEnabled) return
        entranceStart = SystemClock.uptimeMillis()
        entranceIndex = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val press = pressScale ?: PressScale(
            scale = 1.06f,
            liftPx = parent.resources.displayMetrics.density * 6
        ).also { it.enabled = motionEnabled; pressScale = it }

        return when (viewType) {
            AwardItem.TYPE_INFO -> InfoHolder(AdapterAwardInfoHeaderBinding.inflate(inflater, parent, false))
            AwardItem.TYPE_CATEGORY -> AwardsNomineesViewHolder(
                AdapterAwardCategoryRowBinding.inflate(inflater, parent, false),
                nomineePool,
                NomineeAdapter(glide, press, motionEnabled) { nominee, view -> actions.onNomineeClicked(nominee, view) },
                scrollStates
            )
            AwardItem.TYPE_SECTION -> SectionHolder(AdapterAwardSectionTitleBinding.inflate(inflater, parent, false))
            AwardItem.TYPE_TEXT_CARD, AwardItem.TYPE_TEXT_PLAIN ->
                TextHolder(AdapterAwardTextCardBinding.inflate(inflater, parent, false), viewType == AwardItem.TYPE_TEXT_CARD)
            AwardItem.TYPE_VIDEO_ROW -> VideoRowHolder(AdapterAwardMediaRowBinding.inflate(inflater, parent, false), press)
            AwardItem.TYPE_PEOPLE_ROW -> PeopleRowHolder(AdapterAwardMediaRowBinding.inflate(inflater, parent, false), press)
            AwardItem.TYPE_VIDEO_WIDE -> VideoWideHolder(AdapterAwardVideoWideBinding.inflate(inflater, parent, false))
            AwardItem.TYPE_BACKDROP -> BackdropHolder(AdapterAwardVideoWideBinding.inflate(inflater, parent, false))
            else -> ReadMoreHolder(AdapterAwardReadMoreBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_EXPAND) && holder is TextHolder) {
            holder.applyExpanded(getItem(position) as AwardItem.Text)
            return
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is AwardItem.Info -> (holder as InfoHolder).bind(item)
            is AwardItem.Category -> (holder as AwardsNomineesViewHolder).bind(item)
            is AwardItem.Section -> (holder as SectionHolder).bind(item)
            is AwardItem.Text -> (holder as TextHolder).bind(item)
            is AwardItem.Videos -> (holder as VideoRowHolder).bind(item)
            is AwardItem.People -> (holder as PeopleRowHolder).bind(item)
            is AwardItem.Video -> (holder as VideoWideHolder).bind(item)
            is AwardItem.ReadMore -> (holder as ReadMoreHolder).bind(item)
            is AwardItem.Backdrop -> (holder as BackdropHolder).bind(item)
        }
        maybeAnimateEntrance(holder.itemView)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        resetEntrance(holder.itemView)
        when (holder) {
            is AwardsNomineesViewHolder -> holder.saveState()
            is TextHolder -> holder.recycle()
            is VideoWideHolder -> glide.clear(holder.thumb)
            is BackdropHolder -> glide.clear(holder.thumb)
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is AwardsNomineesViewHolder) holder.ensureLaidOut()
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        resetEntrance(holder.itemView)
        return true
    }

    private fun maybeAnimateEntrance(view: View) {
        if (!motionEnabled || entranceStart == 0L) return
        if (SystemClock.uptimeMillis() - entranceStart > ENTRANCE_WINDOW_MS || entranceIndex >= ENTRANCE_MAX_ITEMS) {
            entranceStart = 0L
            return
        }
        val index = entranceIndex++
        view.animate().cancel()
        view.alpha = 0f
        view.translationY = view.resources.getDimension(R.dimen.awards_enter_offset)
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(index * ENTRANCE_STAGGER_MS)
            .setDuration(ENTRANCE_DURATION_MS)
            .setInterpolator(DECELERATE)
            .start()
    }

    private fun resetEntrance(view: View) {
        view.animate().cancel()
        view.alpha = 1f
        view.translationY = 0f
    }

    private fun toggleExpanded(holder: RecyclerView.ViewHolder) {
        val position = holder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION) return
        val key = getItem(position).key
        if (!expandedKeys.remove(key)) expandedKeys.add(key)
        notifyItemChanged(position, PAYLOAD_EXPAND)
    }

    // ---------------------------------------------------------------- ViewHolders

    inner class InfoHolder(private val binding: AdapterAwardInfoHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AwardItem.Info) {
            val award = item.award
            binding.infoName.text = award.name
            binding.infoPresentedBy.text = award.presentedBy
            binding.infoPresentedBy.visibility = if (award.presentedBy.isNullOrBlank()) View.GONE else View.VISIBLE

            val firstAwarded = award.firstAwardedDate?.takeIf { it.isNotBlank() }
                ?.let { itemView.context.getString(R.string.award_first_awarded, it) }
            val meta = listOfNotNull(award.country?.takeIf { it.isNotBlank() }, firstAwarded).joinToString(" · ")
            binding.infoMeta.text = meta
            binding.infoMeta.visibility = if (meta.isBlank()) View.GONE else View.VISIBLE

            Images.model(itemView.context, award.logo)?.let {
                val size = itemView.resources.getDimensionPixelSize(R.dimen.awards_person_size)
                Images.load(glide, binding.infoLogo, it, size, size, crossFade = false)
            }

            listOf(binding.siteImage, binding.facebookImage, binding.twitterImage, binding.instagramImage, binding.youtubeImage)
                .forEach { it.visibility = View.GONE }
            award.socialList?.forEach { social ->
                val button = when (social.type) {
                    SocialType.SITE -> binding.siteImage
                    SocialType.FACEBOOK -> binding.facebookImage
                    SocialType.TWITTER -> binding.twitterImage
                    SocialType.INSTAGRAM -> binding.instagramImage
                    SocialType.YOUTUBE -> binding.youtubeImage
                }
                button.visibility = View.VISIBLE
                button.setOnClickListener { actions.onLinkClicked(social.link) }
            }
        }
    }

    inner class SectionHolder(private val binding: AdapterAwardSectionTitleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AwardItem.Section) {
            binding.sectionTitle.text = item.title
        }
    }

    inner class TextHolder(
        private val binding: AdapterAwardTextCardBinding,
        private val card: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            if (!card) {
                // Parágrafo corrido (histórico): sem cartão, como um artigo.
                binding.root.background = null
                binding.root.setPadding(0, 0, 0, 0)
                binding.textBody.setTextColor(itemView.context.getColor(R.color.awards_on_surface_variant))
                binding.textBody.textSize = 16f
            }
            binding.textToggle.setOnClickListener { toggleExpanded(this) }
            binding.textBody.setOnClickListener(null)
        }

        fun bind(item: AwardItem.Text) {
            val context = itemView.context
            binding.textTitle.text = item.title?.styledString()
            binding.textTitle.visibility = if (item.title.isNullOrBlank()) View.GONE else View.VISIBLE

            binding.textBody.setResourceStyledText(item.html)
            binding.textBody.setupLinkableTextView(context)
            binding.textBody.visibility = if (item.html.isNullOrBlank()) View.GONE else View.VISIBLE

            binding.textCredits.text = item.credits?.styledString()
            binding.textCredits.visibility = if (item.credits.isNullOrBlank()) View.GONE else View.VISIBLE

            val image = item.image?.let { Images.model(context, it) }
            if (image != null) {
                binding.textImage.visibility = View.VISIBLE
                val width = context.resources.displayMetrics.widthPixels
                val height = context.resources.getDimensionPixelSize(R.dimen.awards_video_height)
                Images.load(glide, binding.textImage, image, width, height, crossFade = motionEnabled)
            } else {
                glide.clear(binding.textImage)
                binding.textImage.visibility = View.GONE
            }

            val click = item.click
            if (click != null) {
                binding.textAction.visibility = View.VISIBLE
                binding.textAction.text = click.buttonText ?: context.getString(R.string.click_here_to_go)
                binding.textAction.setOnClickListener { actions.onBlockClicked(click) }
            } else {
                binding.textAction.visibility = View.GONE
                binding.textAction.setOnClickListener(null)
            }

            applyExpanded(item)
        }

        fun applyExpanded(item: AwardItem.Text) {
            val canCollapse = item.collapsible && (item.html?.length ?: 0) > COLLAPSE_MIN_CHARS
            val expanded = !canCollapse || expandedKeys.contains(item.key)
            binding.textBody.maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_LINES
            binding.textToggle.visibility = if (canCollapse) View.VISIBLE else View.GONE
            binding.textToggle.setText(if (expanded) R.string.award_read_less else R.string.award_read_more)
        }

        fun recycle() {
            glide.clear(binding.textImage)
        }
    }

    inner class VideoRowHolder(
        private val binding: AdapterAwardMediaRowBinding,
        private val press: PressScale
    ) : RecyclerView.ViewHolder(binding.root) {

        private val adapter = VideoAdapter()

        init {
            val res = itemView.resources
            val density = res.displayMetrics.density
            binding.rowList.layoutParams = binding.rowList.layoutParams.apply {
                height = res.getDimensionPixelSize(R.dimen.awards_video_height) +
                    (52 * density).toInt() + 2 * res.getDimensionPixelSize(R.dimen.awards_row_vertical_padding)
            }
            binding.rowList.setHasFixedSize(true)
            binding.rowList.setRecycledViewPool(mediaPool)
            binding.rowList.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false).apply {
                initialPrefetchItemCount = 2
            }
            binding.rowList.itemAnimator = null
            binding.rowList.adapter = adapter
        }

        fun bind(item: AwardItem.Videos) {
            binding.rowTitle.text = item.title
            adapter.items = item.videos
        }

        inner class VideoAdapter : RecyclerView.Adapter<VideoHolder>() {
            var items: List<ContentVideo> = emptyList()
                set(value) {
                    if (field === value) return
                    field = value
                    notifyDataSetChanged()
                }

            override fun getItemCount(): Int = items.size
            override fun getItemViewType(position: Int): Int = MEDIA_VIDEO

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder =
                VideoHolder(AdapterAwardVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), press)

            override fun onBindViewHolder(holder: VideoHolder, position: Int) = holder.bind(items[position])

            override fun onViewRecycled(holder: VideoHolder) = holder.recycle()
        }
    }

    inner class VideoHolder(
        private val binding: AdapterAwardVideoItemBinding,
        press: PressScale
    ) : RecyclerView.ViewHolder(binding.root) {

        private var videoId: String? = null
        private val width = itemView.resources.getDimensionPixelSize(R.dimen.awards_video_width)
        private val height = itemView.resources.getDimensionPixelSize(R.dimen.awards_video_height)

        init {
            (binding.videoRoot.layoutParams as ViewGroup.MarginLayoutParams).marginEnd =
                itemView.resources.getDimensionPixelSize(R.dimen.awards_card_spacing)
            binding.videoRoot.setOnClickListener { videoId?.let { actions.onVideoClicked(it) } }
            binding.videoRoot.setOnTouchListener(press)
            binding.videoRoot.onFocusChangeListener = press
        }

        fun bind(video: ContentVideo) {
            videoId = video.videoId
            binding.videoTitle.text = video.information.contentTitle?.styledString()
            binding.videoRoot.contentDescription = video.information.contentTitle
            Images.load(glide, binding.videoThumb, youtubeThumb(video.videoId), width, height, crossFade = motionEnabled)
        }

        fun recycle() {
            PressScale.reset(binding.videoRoot)
            glide.clear(binding.videoThumb)
        }
    }

    inner class PeopleRowHolder(
        private val binding: AdapterAwardMediaRowBinding,
        private val press: PressScale
    ) : RecyclerView.ViewHolder(binding.root) {

        private val adapter = PersonAdapter()

        init {
            val res = itemView.resources
            val density = res.displayMetrics.density
            binding.rowList.layoutParams = binding.rowList.layoutParams.apply {
                height = res.getDimensionPixelSize(R.dimen.awards_person_size) +
                    (44 * density).toInt() + 2 * res.getDimensionPixelSize(R.dimen.awards_row_vertical_padding)
            }
            binding.rowList.setHasFixedSize(true)
            binding.rowList.setRecycledViewPool(mediaPool)
            binding.rowList.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false).apply {
                initialPrefetchItemCount = 4
            }
            binding.rowList.itemAnimator = null
            binding.rowList.adapter = adapter
        }

        fun bind(item: AwardItem.People) {
            binding.rowTitle.text = item.title
            binding.rowTitle.visibility = if (item.title.isNullOrBlank()) View.GONE else View.VISIBLE
            adapter.items = item.persons
        }

        inner class PersonAdapter : RecyclerView.Adapter<PersonHolder>() {
            var items: List<Person> = emptyList()
                set(value) {
                    if (field === value) return
                    field = value
                    notifyDataSetChanged()
                }

            override fun getItemCount(): Int = items.size
            override fun getItemViewType(position: Int): Int = MEDIA_PERSON

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonHolder =
                PersonHolder(AdapterAwardPersonItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), press)

            override fun onBindViewHolder(holder: PersonHolder, position: Int) = holder.bind(items[position])

            override fun onViewRecycled(holder: PersonHolder) = holder.recycle()
        }
    }

    inner class PersonHolder(
        private val binding: AdapterAwardPersonItemBinding,
        press: PressScale
    ) : RecyclerView.ViewHolder(binding.root) {

        private var personId: Int? = null
        private val size = itemView.resources.getDimensionPixelSize(R.dimen.awards_person_size)

        init {
            binding.personRoot.setOnClickListener { personId?.let { actions.onPersonClicked(it, binding.personPhoto) } }
            binding.personRoot.setOnTouchListener(press)
            binding.personRoot.onFocusChangeListener = press
        }

        fun bind(person: Person) {
            personId = person.id
            binding.personName.text = person.name
            binding.personInitials.text = NomineeAdapter.initialsOf(person.name.orEmpty())
            Images.load(glide, binding.personPhoto, Images.tmdb(person.profilePath, ImageSize.PROFILE_185), size, size, crossFade = motionEnabled)
        }

        fun recycle() {
            PressScale.reset(binding.personRoot)
            glide.clear(binding.personPhoto)
        }
    }

    inner class VideoWideHolder(private val binding: AdapterAwardVideoWideBinding) : RecyclerView.ViewHolder(binding.root) {

        val thumb get() = binding.videoThumb
        private var videoId: String? = null

        init {
            binding.videoCard.setOnClickListener { videoId?.let { actions.onVideoClicked(it) } }
        }

        fun bind(item: AwardItem.Video) {
            val video = item.video
            videoId = video.videoId
            binding.videoTitle.text = video.information.contentTitle?.styledString()
            binding.videoTitle.visibility = if (video.information.contentTitle.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.videoText.text = video.information.contentText?.styledString()
            binding.videoText.visibility = if (video.information.contentText.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.videoCard.contentDescription = video.information.contentTitle

            val width = itemView.resources.displayMetrics.widthPixels
            Images.load(glide, binding.videoThumb, youtubeThumb(video.videoId), width, width * 9 / 16, crossFade = motionEnabled)
        }
    }

    /** Imagem larga de um filme vencedor, entre as categorias. Toque abre o filme. */
    inner class BackdropHolder(private val binding: AdapterAwardVideoWideBinding) : RecyclerView.ViewHolder(binding.root) {

        val thumb get() = binding.videoThumb
        private var movie: com.tiagohs.entities.awards.Nominee? = null

        init {
            binding.videoPlay.visibility = View.GONE
            binding.videoCard.setOnClickListener { v -> movie?.let { actions.onNomineeClicked(it, v) } }
        }

        fun bind(item: AwardItem.Backdrop) {
            movie = item.movie
            val ctx = itemView.context
            binding.videoTitle.text = item.movie.name
            binding.videoTitle.visibility = View.VISIBLE
            val person = item.winner.takeIf { it !== item.movie }?.name
            val label = ctx.getString(R.string.award_backdrop_winner, item.category.orEmpty())
            binding.videoText.text = if (person.isNullOrBlank()) label else "$label · $person"
            binding.videoText.visibility = View.VISIBLE
            binding.videoCard.contentDescription = item.movie.name

            val width = itemView.resources.displayMetrics.widthPixels
            Images.load(glide, binding.videoThumb, Images.tmdb(item.movie.backdropPath, ImageSize.BACKDROP_780),
                width, width * 9 / 16, crossFade = motionEnabled)
        }
    }

    inner class ReadMoreHolder(private val binding: AdapterAwardReadMoreBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.readMoreButton.setOnClickListener { actions.onReadMoreClicked() }
        }

        fun bind(item: AwardItem.ReadMore) {
            binding.readMoreButton.setText(if (item.expanded) R.string.award_read_less else R.string.award_read_more)
        }
    }

    companion object {
        private const val PAYLOAD_EXPAND = "expand"
        private const val COLLAPSED_LINES = 4
        private const val COLLAPSE_MIN_CHARS = 220
        private const val MEDIA_VIDEO = 10
        private const val MEDIA_PERSON = 11

        private const val ENTRANCE_WINDOW_MS = 600L
        private const val ENTRANCE_MAX_ITEMS = 6
        private const val ENTRANCE_STAGGER_MS = 60L
        private const val ENTRANCE_DURATION_MS = 360L
        private val DECELERATE = DecelerateInterpolator(1.8f)

        fun youtubeThumb(videoId: String): String = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

        private val DIFF = object : DiffUtil.ItemCallback<AwardItem>() {
            override fun areItemsTheSame(oldItem: AwardItem, newItem: AwardItem): Boolean =
                oldItem.key == newItem.key && oldItem.viewType == newItem.viewType

            override fun areContentsTheSame(oldItem: AwardItem, newItem: AwardItem): Boolean = when {
                oldItem is AwardItem.ReadMore && newItem is AwardItem.ReadMore -> oldItem.expanded == newItem.expanded
                else -> oldItem === newItem
            }
        }
    }
}

/**
 * Sem animação de inserir/remover (as fileiras novas já entram escalonadas pelo adapter e um fade
 * extra brigaria com isso); mantém as de mudança/movimento ("Ler mais" empurra a lista suavemente).
 */
class AwardItemAnimator : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        dispatchAddFinished(holder)
        return false
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        dispatchRemoveFinished(holder)
        return false
    }
}
