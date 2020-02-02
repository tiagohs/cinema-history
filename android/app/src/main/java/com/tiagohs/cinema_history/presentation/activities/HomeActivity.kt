package com.tiagohs.cinema_history.presentation.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.tiagohs.cinema_history.R
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.startActivityWithSlideAnimation
import com.tiagohs.entities.image.Image
import com.tiagohs.entities.image.ImageResize
import com.tiagohs.entities.image.ImageStyle
import com.tiagohs.cinema_history.presentation.configs.BaseActivity
import com.tiagohs.entities.enums.ImageType
import com.tiagohs.entities.enums.MainTopicsType
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity: BaseActivity() {

    override fun onGetLayoutViewId(): Int = R.layout.activity_home
    override fun onGetMenuLayoutId(): Int = R.menu.menu_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar(toolbar, displayHomeAsUpEnabled = false, displayShowTitleEnabled = false)
        setupHistoryCinema()
        setupMilMovies()
        setupTimeline()
        setupDirectors()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                startActivityWithSlideAnimation(SettingActivity.newIntent(this))
                return true
            }
            R.id.action_about -> {
                startActivityWithSlideAnimation(AboutActivty.newIntent(this))
                return true
            }
            R.id.action_references -> {
                startActivityWithSlideAnimation(ReferenceActivity.newIntent(this))
                return true
            }

            else -> return false
        }

    }

    private fun setupHistoryCinema() {
        val historyImage = Image(ImageType.LOCAL, "img_chinatown",null)
        historyMovieImage.loadImage(historyImage, null)

        cinemaImageContainer.setOnClickListener(onHistoryCinemaClick())
        cinemaTitleContainer.setOnClickListener(onHistoryCinemaClick())
        cinemaFooterContainer.setOnClickListener(onHistoryCinemaClick())
        startHistoryButton.setOnClickListener(onHistoryCinemaClick())
    }

    private fun setupMilMovies() {
        val imageStyle = ImageStyle(resize = ImageResize(height = 500), scaleType = "center_crop")
        val milMoviesImage = Image(ImageType.LOCAL,"img_godfather",null, imageStyle)

        moviesImage.loadImage(milMoviesImage, null)

        moviesCard.setOnClickListener(onMilMoviesClick())
    }

    private fun setupTimeline() {
        val imageStyle = ImageStyle(resize = ImageResize(height = 350), scaleType = "center_crop")
        val timelineImageObject = Image(ImageType.LOCAL,"img_film_faixa",null, imageStyle)

        timelineImage.loadImage(timelineImageObject, null)

        timelineCard.setOnClickListener(onTimelineCinemaClick())
    }

    private fun setupDirectors() {
        val imageStyle = ImageStyle(resize = ImageResize(height = 450), scaleType = "center_crop")
        val image = Image(ImageType.LOCAL,"img_david_fincher",null, imageStyle)

        directorsImage.loadImage(image, null)

        directorsCard.setOnClickListener(onDirectorsClick())
    }

    private fun onHistoryCinemaClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivityWithSlideAnimation(MainTopicsActivity.newIntent(MainTopicsType.HISTORY_CINEMA, this, darkMode = true))
        }
    }

    private fun onMilMoviesClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivityWithSlideAnimation(MainTopicsActivity.newIntent(MainTopicsType.MIL_MOVIES, this))
        }
    }

    private fun onTimelineCinemaClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivityWithSlideAnimation(TimelineActivity.newIntent(this))
        }
    }

    private fun onDirectorsClick(): View.OnClickListener {
        return View.OnClickListener {
            startActivityWithSlideAnimation(MainTopicsActivity.newIntent(MainTopicsType.DIRECTORS, this))
        }
    }
}
