package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.models.Page
import com.tiagohs.cinema_history.models.Sumario
import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.ui.adapters.SumarioPresentationAdapter
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import kotlinx.android.synthetic.main.activity_presentation.*
import kotlinx.android.synthetic.main.activity_presentation.toolbar

class PresentationActivity: BaseActivity() {
    override fun onGetLayoutViewId(): Int = R.layout.activity_presentation

    override fun onGetMenuLayoutId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val mainTopic = intent.getSerializableExtra(MAIN_TOPIC) as? MainTopicItem

        quoteText.text = mainTopic?.quote?.quote
        quoteTextAuthor.text = mainTopic?.quote?.author

        val drawable = resources
            .getIdentifier(mainTopic?.image?.url, "drawable", packageName)
        val width = resources.configuration.screenWidthDp

        Picasso.get()
            .load(drawable)
            .centerInside()
            .resize(width, 350.convertIntToDp(this))
            .into(image)

        mainTopicTitle.text = mainTopic?.title
        mainTopicDescription.text = mainTopic?.description

        sumarioList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val adapter = SumarioPresentationAdapter(this, listOf(
            Sumario(1, "Pioneiros do Cinema", "A transformação do cinema, de show de curiosidades no início do século XX a atividade artistica, testemunhou o lançamento de centenas e, logo de milhares de filmes em todo o mundo. "),
            Sumario(1, "Pioneiros do Cinema", "A transformação do cinema, de show de curiosidades no início do século XX a atividade artistica, testemunhou o lançamento de centenas e, logo de milhares de filmes em todo o mundo. "),
            Sumario(1, "Pioneiros do Cinema", "A transformação do cinema, de show de curiosidades no início do século XX a atividade artistica, testemunhou o lançamento de centenas e, logo de milhares de filmes em todo o mundo. "),
            Sumario(1, "Pioneiros do Cinema", "A transformação do cinema, de show de curiosidades no início do século XX a atividade artistica, testemunhou o lançamento de centenas e, logo de milhares de filmes em todo o mundo. "),
            Sumario(1, "Pioneiros do Cinema", "A transformação do cinema, de show de curiosidades no início do século XX a atividade artistica, testemunhou o lançamento de centenas e, logo de milhares de filmes em todo o mundo. "),
            Sumario(1, "Pioneiros do Cinema", "A transformação do cinema, de show de curiosidades no início do século XX a atividade artistica, testemunhou o lançamento de centenas e, logo de milhares de filmes em todo o mundo. ")
        ))
        adapter.onSumarioClick = {
            startActivity(HistoryPagesActivity.newIntent(this,
                listOf(
                    Page(1, listOf()),
                    Page(1, listOf()),
                    Page(1, listOf()),
                    Page(1, listOf()),
                    Page(1, listOf())
                )
            ))
        }

        sumarioList.adapter = adapter
    }

    companion object {

        const val MAIN_TOPIC = "MAIN_TOPIC"

        fun newInstance(context: Context, mainTopic: MainTopicItem): Intent {
            val intent = Intent(context, PresentationActivity::class.java)

            intent.putExtra(MAIN_TOPIC, mainTopic)

            return intent
        }
    }
}