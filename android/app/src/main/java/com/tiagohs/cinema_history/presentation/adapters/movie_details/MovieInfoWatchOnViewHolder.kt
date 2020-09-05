package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.NetworkType
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.adapter_movie_info_watch_on.*
import kotlinx.android.synthetic.main.view_network_item.view.*

class MovieInfoWatchOnViewHolder(
    view: View
) : BaseViewHolder<MovieInfo>(view) {

    private var isSetup = false

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val watchOn = item.movie.extraInfo?.watchOn ?: return

        if (!isSetup) {
            watchOn.forEach { network ->
                val color = network.type.color
                val textColor = network.type.textColor
                val view = LayoutInflater.from(containerView.context)
                    .inflate(R.layout.view_network_item, null, false)
                val layoutParams = Constraints.LayoutParams(
                    Constraints.LayoutParams.WRAP_CONTENT,
                    Constraints.LayoutParams.WRAP_CONTENT
                )

                view.layoutParams = layoutParams
                view.networkName.setResourceTextColor(textColor)

                if (network.type == NetworkType.UNKNOWN) {
                    view.networkName.setResourceText(network.name)
                } else {
                    network.type.networkName?.let { view.networkName.setResourceText(it) }
                }

                view.networkContainer.setOnClickListener {
                    containerView.context.openLink(network.link)
                }

                view.networkContainerCard.setCardBackgroundColor(containerView.context.getResourceColor(color))

                watchOnContainer.addView(view)
            }

            isSetup = true
        }

    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_watch_on
    }
}