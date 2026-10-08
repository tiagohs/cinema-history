package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ViewNetworkItemBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoWatchOnBinding
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.NetworkType
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.helpers.extensions.*

class MovieInfoWatchOnViewHolder(
    private val binding: AdapterMovieInfoWatchOnBinding
) : BaseViewHolder<MovieInfo>(binding) {

    private var isSetup = false

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val watchOn = item.movie.extraInfo?.watchOn ?: return

        if (!isSetup) {
            watchOn.forEach { network ->
                val color = network.type.color
                val textColor = network.type.textColor
                val itemBinding = ViewNetworkItemBinding.inflate(LayoutInflater.from(itemView.context), null, false)
                val layoutParams = Constraints.LayoutParams(
                    Constraints.LayoutParams.WRAP_CONTENT,
                    Constraints.LayoutParams.WRAP_CONTENT
                )

                itemBinding.root.layoutParams = layoutParams
                itemBinding.networkName.setResourceTextColor(textColor)

                if (network.type == NetworkType.UNKNOWN) {
                    itemBinding.networkName.setResourceText(network.name)
                } else {
                    network.type.networkName?.let { itemBinding.networkName.setResourceText(it) }
                }

                itemBinding.networkContainer.setOnClickListener {
                    itemView.context.openLink(network.link)
                }

                itemBinding.networkContainerCard.setCardBackgroundColor(itemView.context.getResourceColor(color))

                binding.watchOnContainer.addView(itemBinding.root)
            }

            isSetup = true
        }

    }
}