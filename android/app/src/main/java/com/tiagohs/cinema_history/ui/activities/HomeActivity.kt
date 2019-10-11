package com.tiagohs.cinema_history.ui.activities

import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.MainTopicsType
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import com.tiagohs.hqr.helpers.tools.AppBarListener
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity :
    BaseActivity() {

    override fun onGetLayoutViewId(): Int = R.layout.activity_home
    override fun onGetMenuLayoutId(): Int = 0

    var isMoviesCardAnimating = false
    var isTimelineCardAnimating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)

        val width = resources.configuration.screenWidthDp

        Picasso.get()
            .load(R.drawable.img_chinatown)
            .centerInside()
            .resize(width, 500.convertIntToDp(this))
            .into(historyMovieImage)

        Picasso.get()
            .load(R.drawable.img_goodfather)
            .centerCrop()
            .resize(width, 500.convertIntToDp(this))
            .into(moviesImage)

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
            startActivity(MainTopicsActivity.newIntent(MainTopicsType.MILL_MOVIES, this))
        }
    }

    private fun onTimelineCinemaClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivity(MainTopicsActivity.newIntent(MainTopicsType.TIMELINE, this))
        }
    }

}
