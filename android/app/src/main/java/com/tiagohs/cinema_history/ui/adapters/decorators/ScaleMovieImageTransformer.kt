package com.tiagohs.cinema_history.ui.adapters.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.adapter_movie_list.view.*
import kotlin.math.abs

class ScaleMovieImageTransformer(
    private val horizontalSpace: Int,
    private val spaceBetweenItems: Int
) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {

        page.apply {
            val pageTranslationX = spaceBetweenItems + horizontalSpace

            translationX = - pageTranslationX * position

            imageCard?.scaleY = (1 - (MIN_SCALE_Y * abs(position)))
            imageCard?.scaleX = (1 - (MIN_SCALE_X * abs(position)))

            if (position >= -1 && position <= 1) { // [-1,1]
                originalTitle.translationX = (position) * (width / 4).toFloat()
                title.translationX = (position) * (width / 2).toFloat()
            } else {
                originalTitle.translationX = (position) * (width / 4).toFloat()
                title.translationX = (position) * (width / 2).toFloat()
            }
        }
    }

    companion object {
        const val MIN_SCALE_X = 0.25f
        const val MIN_SCALE_Y = 0.20f
    }

    class HorizontalMarginItemDecoration(val horizontalSpace: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.right = horizontalSpace
        }

    }
}