package com.tiagohs.cinema_history.presentation.adapters.decorators

import android.graphics.Rect
import android.view.View
import com.tiagohs.cinema_history.databinding.AdapterMovieListBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class ScaleMovieImageTransformer(
    private val horizontalSpace: Int,
    private val spaceBetweenItems: Int
) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {

        val binding = AdapterMovieListBinding.bind(page)

        page.apply {
            val pageTranslationX = spaceBetweenItems + horizontalSpace

            translationX = - pageTranslationX * position

            binding.imageCard.scaleY = (1 - (MIN_SCALE_Y * abs(position)))
            binding.imageCard.scaleX = (1 - (MIN_SCALE_X * abs(position)))

            if (position >= -1 && position <= 1) { // [-1,1]
                binding.originalTitle.translationX = (position) * (width / 4).toFloat()
                binding.title.translationX = (position) * (width / 2).toFloat()
            } else {
                binding.originalTitle.translationX = (position) * (width / 4).toFloat()
                binding.title.translationX = (position) * (width / 2).toFloat()
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