package com.tiagohs.cinema_history.ui.custom

import android.view.View
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.tiagohs.cinema_history.R

class ParallaxImageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {

            if (position >= -1 && position <= 1) { // [-1,1]
                val image = page.findViewById<ImageView>(R.id.image)

                image?.translationX = -position * width
            }
        }
    }

}