package com.tiagohs.cinema_history.ui.custom

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import com.tiagohs.cinema_history.helpers.extensions.convertIntToPixel
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log


class MoviesViewHolder: ViewPager {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        super.onPageScrolled(position, offset, offsetPixels)

        Log.d("Scroll", "${position} - ${offset} - ${offsetPixels}")
    }
}