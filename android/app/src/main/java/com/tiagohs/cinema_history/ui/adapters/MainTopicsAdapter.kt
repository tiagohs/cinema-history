package com.tiagohs.cinema_history.ui.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.models.MainTopic
import kotlinx.android.synthetic.main.adapter_main_topics.view.*
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.utils.AnimationUtils


class MainTopicsAdapter(
    val context: Context?,
    val mainTopicList: List<MainTopic>
): RecyclerView.Adapter<MainTopicsAdapter.MainTopicViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainTopicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_topics, parent, false)

        return MainTopicViewHolder(view)
    }

    override fun getItemCount(): Int = mainTopicList.size

    override fun onBindViewHolder(holder: MainTopicViewHolder, position: Int) {
        val mainTopic = mainTopicList.get(position)

        holder.bind(mainTopic)
    }

    override fun onViewAttachedToWindow(holder: MainTopicViewHolder) {
        super.onViewAttachedToWindow(holder)

        holder.setupAnimation()
    }


    inner class MainTopicViewHolder(
        val view: View
    ): RecyclerView.ViewHolder(view) {

        var mainTopic: MainTopic? = null

        fun bind(mainTopic: MainTopic) {
            this.mainTopic = mainTopic

            val context = context ?: return

            when (mainTopic.image.imageType) {
                ImageType.ONLINE -> {
                    Picasso.get()
                        .load(mainTopic.image.url)
                        .into(itemView.mainImage)
                }
                ImageType.LOCAL -> {
                    val drawable = context.resources
                        .getIdentifier(mainTopic.image.url, "drawable", context.packageName)

                    val width = context.resources.configuration.screenWidthDp

                    Picasso.get()
                        .load(drawable)
                        .centerInside()
                        .resize(width, 250.convertIntToDp(context))
                        .into(itemView.mainImage)
                }
            }

            val backgroundColor = context.resources
                .getIdentifier(mainTopic.titleBackgroundColor, "color", context.packageName)
            val textColor = context.resources
                .getIdentifier(mainTopic.titleColor, "color", context.packageName)

            itemView.title.text = mainTopic.title
            itemView.title.setTextColor(context.resources.getColor(textColor))
            itemView.contentBackground.setBackgroundColor(context.resources.getColor(backgroundColor))
            itemView.description.text = mainTopic.description
            itemView.description.setTextColor(context.resources.getColor(textColor))

            val background = GradientDrawable()
            background.cornerRadius = 10f

            itemView.mainTopicsContainer.background = background
        }

        fun setupAnimation() {
            val mainTopicAnimation = mainTopic?.image?.animation ?: return
            val animation = AnimationUtils.createAnimationFromType(mainTopicAnimation.type, mainTopicAnimation.duration)

            itemView.mainImage.clearAnimation()
            itemView.mainImage.startAnimation(animation)
        }
    }
}