package com.tiagohs.cinema_history.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.enums.MainTopicsType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.helpers.extensions.loadImage
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.models.image.ImageResize
import com.tiagohs.cinema_history.models.image.ImageStyle
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.hqr.helpers.tools.AppBarListener
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity :
    BaseActivity() {

    override fun onGetLayoutViewId(): Int = R.layout.activity_home
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)

        val historyImage = Image(
            ImageType.LOCAL,
            "img_chinatown",
            null,
            ImageStyle(
                resize = ImageResize(
                    height = 500
                ),
                scaleType = "center_inside")
        )

        historyMovieImage.loadImage(historyImage)

        val milMoviesImage = Image(
            ImageType.LOCAL,
            "img_goodfather",
            null,
            ImageStyle(
                resize = ImageResize(
                    height = 500
                ),
                scaleType = "center_crop")
        )

        moviesImage.loadImage(milMoviesImage)

        val timelineImageObject = Image(
            ImageType.LOCAL,
            "img_faixa",
            null,
            ImageStyle(
                resize = ImageResize(
                    height = 300
                ),
                scaleType = "center_inside")
        )

        timelineImage.loadImage(timelineImageObject)

        cinemaImageContainer.setOnClickListener(onHistoryCinemaClick())
        cinemaTitleContainer.setOnClickListener(onHistoryCinemaClick())
        cinemaFooterContainer.setOnClickListener(onHistoryCinemaClick())
        startHistoryButton.setOnClickListener(onHistoryCinemaClick())

        moviesCard.setOnClickListener(onMilMoviesClick())

        timelineCard.setOnClickListener(onTimelineCinemaClick())
    }

    private fun onHistoryCinemaClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivity(MainTopicsActivity.newIntent(MainTopicsType.HISTORY_CINEMA, this, darkMode = true))
        }
    }

    private fun onMilMoviesClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivity(MainTopicsActivity.newIntent(MainTopicsType.MIL_MOVIES, this))
        }
    }

    private fun onTimelineCinemaClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivity(TimelineActivity.newIntent(this))
        }
    }

}
