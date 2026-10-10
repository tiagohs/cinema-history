package com.tiagohs.cinema_history.presentation.adapters

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.databinding.AdapterAwardNomineesItemBinding
import com.tiagohs.cinema_history.presentation.views.Images
import com.tiagohs.cinema_history.presentation.views.PressScale
import com.tiagohs.entities.awards.Nominee
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.enums.NomineeType

/**
 * Fileira horizontal de uma categoria (estilo das "rows" dos apps de streaming).
 * O vencedor usa um cartão maior, com borda e selo dourados e um leve brilho; os indicados vêm depois.
 */
class NomineeAdapter(
    private val glide: RequestManager,
    private val pressScale: PressScale,
    private val motionEnabled: Boolean,
    private val onNomineeClicked: ((nominee: Nominee, sharedView: View) -> Unit)?
) : ListAdapter<Nominee, NomineeAdapter.NomineeViewHolder>(DIFF) {

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).winner == true) TYPE_WINNER else TYPE_NOMINEE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NomineeViewHolder {
        val binding = AdapterAwardNomineesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NomineeViewHolder(binding, viewType == TYPE_WINNER)
    }

    override fun onBindViewHolder(holder: NomineeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: NomineeViewHolder) {
        holder.recycle()
    }

    inner class NomineeViewHolder(
        private val binding: AdapterAwardNomineesItemBinding,
        private val isWinner: Boolean
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private val res = itemView.resources
        private val widthPx = res.getDimensionPixelSize(if (isWinner) R.dimen.awards_winner_card_width else R.dimen.awards_card_width)
        private val heightPx = res.getDimensionPixelSize(if (isWinner) R.dimen.awards_winner_card_height else R.dimen.awards_card_height)
        private var item: Nominee? = null

        init {
            // Tamanho definido uma vez por tipo de ViewHolder (nada de mudar LayoutParams no bind).
            binding.posterFrame.layoutParams = binding.posterFrame.layoutParams.apply {
                width = widthPx
                height = heightPx
            }
            binding.nomineeRoot.layoutParams = (binding.nomineeRoot.layoutParams as ViewGroup.MarginLayoutParams).apply {
                marginEnd = res.getDimensionPixelSize(R.dimen.awards_card_spacing)
            }

            if (isWinner) {
                binding.posterCard.strokeWidth = res.getDimensionPixelSize(R.dimen.awards_winner_stroke)
                binding.posterCard.cardElevation = res.getDimension(R.dimen.awards_winner_glow)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    // Sombra dourada = "brilho" do vencedor, desenhado pelo RenderThread.
                    val gold = itemView.context.getColor(R.color.awards_gold)
                    binding.posterCard.outlineSpotShadowColor = gold
                    binding.posterCard.outlineAmbientShadowColor = gold
                }
                binding.winnerMessage.visibility = View.VISIBLE
                binding.posterShade.visibility = View.VISIBLE
                binding.nomineesTitle.setTextColor(itemView.context.getColor(R.color.awards_gold))
            }

            binding.nomineeRoot.setOnClickListener(this)
            binding.nomineeRoot.setOnTouchListener(pressScale)
            binding.nomineeRoot.onFocusChangeListener = pressScale
        }

        fun bind(nominee: Nominee) {
            item = nominee

            binding.nomineesTitle.text = nominee.name
            val subtitle = subtitleOf(nominee)
            binding.nomineesSubtitle.text = subtitle
            binding.nomineesSubtitle.visibility = if (subtitle.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.nomineeRoot.contentDescription = if (isWinner)
                itemView.context.getString(R.string.award_winner_of, nominee.name ?: "")
            else nominee.name

            // O "cartaz" com o nome fica visível até o pôster chegar; some só quando a imagem carrega
            // (se o download falhar, ele continua lá). INVISIBLE/VISIBLE não pedem novo layout.
            showPlaceholder(nominee)
            val url = imageUrl(nominee)
            if (url == null) {
                glide.clear(binding.image)
                binding.image.setImageDrawable(null)
            } else {
                Images.load(glide, binding.image, url, widthPx, heightPx, crossFade = motionEnabled, listener = imageListener)
            }
        }

        /** Um listener por ViewHolder (criado uma vez), não por bind. */
        private val imageListener = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean = false

            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                binding.placeholder.visibility = View.INVISIBLE
                return false
            }
        }

        fun recycle() {
            PressScale.reset(binding.nomineeRoot)
            glide.clear(binding.image)
            item = null
        }

        private fun imageUrl(nominee: Nominee): String? {
            if (nominee.imagePath.isNullOrBlank()) return null
            val size = when {
                nominee.type == NomineeType.PERSON -> if (isWinner) ImageSize.PROFILE_632 else ImageSize.PROFILE_185
                isWinner -> ImageSize.POSTER_500
                else -> ImageSize.POSTER_342
            }
            return Images.tmdb(nominee.imagePath, size)
        }

        /** Sem imagem (ou sem id do TMDB): um "cartaz" com cor derivada do nome, iniciais e o título. */
        private fun showPlaceholder(nominee: Nominee) {
            val name = nominee.name.orEmpty()
            binding.placeholder.visibility = View.VISIBLE
            binding.placeholder.setBackgroundColor(PLACEHOLDER_COLORS[(name.hashCode() and Int.MAX_VALUE) % PLACEHOLDER_COLORS.size])
            binding.placeholderInitials.text = initialsOf(name)
            binding.placeholderName.text = name
        }

        private fun subtitleOf(nominee: Nominee): String? = when (nominee.type) {
            NomineeType.PERSON -> nominee.movie?.name ?: nominee.department ?: nominee.country
            NomineeType.MOVIE -> nominee.director ?: nominee.country
        }

        override fun onClick(v: View) {
            val nominee = item ?: return
            onNomineeClicked?.invoke(nominee, binding.posterCard)
        }
    }

    companion object {
        const val TYPE_NOMINEE = 0
        const val TYPE_WINNER = 1

        private val PLACEHOLDER_COLORS = intArrayOf(
            Color.rgb(0x4A, 0x2E, 0x2E),
            Color.rgb(0x2C, 0x3A, 0x4F),
            Color.rgb(0x4A, 0x3F, 0x22),
            Color.rgb(0x26, 0x40, 0x36),
            Color.rgb(0x42, 0x2A, 0x45),
            Color.rgb(0x33, 0x33, 0x40)
        )

        fun initialsOf(name: String): String {
            val builder = StringBuilder(2)
            for (word in name.split(' ', '-')) {
                val c = word.firstOrNull { it.isLetterOrDigit() } ?: continue
                builder.append(c.uppercaseChar())
                if (builder.length == 2) break
            }
            return builder.toString()
        }

        private val DIFF = object : DiffUtil.ItemCallback<Nominee>() {
            override fun areItemsTheSame(oldItem: Nominee, newItem: Nominee): Boolean =
                oldItem.type == newItem.type && oldItem.id == newItem.id && oldItem.name == newItem.name &&
                    oldItem.movie?.id == newItem.movie?.id

            override fun areContentsTheSame(oldItem: Nominee, newItem: Nominee): Boolean =
                oldItem.winner == newItem.winner && oldItem.imagePath == newItem.imagePath &&
                    oldItem.director == newItem.director && oldItem.movie?.name == newItem.movie?.name
        }
    }
}
