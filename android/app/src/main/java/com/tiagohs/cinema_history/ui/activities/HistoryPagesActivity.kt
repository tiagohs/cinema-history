package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.text.Html
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.enums.ImageType
import com.tiagohs.cinema_history.helpers.extensions.styledString
import com.tiagohs.cinema_history.models.Page
import com.tiagohs.cinema_history.models.image.Image
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history_pages.*

class HistoryPagesActivity: BaseActivity(),
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnCompletionListener {

    override fun onCompletion(mp: MediaPlayer?) {

    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {

    }

    override fun onGetLayoutViewId(): Int = R.layout.activity_history_pages
    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        val textTest = "A <strong>transformação</strong> do cinema, de show de curiosidades no início do século XX a atividade artistica, testemunhou o lançamento de centenas e, logo de milhares de filmes em todo o mundo. Destes, alguns vieram a ser reconhecidos pelos críticos por seu significado e seu caráter inovador, uma vasta quantidade foi efetivamente vista e consumida pelo grande público. mas a maioria caiu na obscuridade, assistida por não mais do que um punhado de pessoas além das envolvidas em sua produção."
//        text.text = textTest.styledString()

        val pageArgument = intent.getSerializableExtra(PAGES) as? Array<Page>
        val pages = pageArgument?.toList()

        if (pages != null) {

        }

    }

    companion object {

        const val PAGES = "PAGES"

        fun newIntent(context: Context, pages: List<Page>): Intent {
            val intent = Intent(context, HistoryPagesActivity::class.java)

            intent.putExtra(PAGES, pages.toTypedArray())

            return intent
        }
    }
}