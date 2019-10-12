package com.tiagohs.cinema_history.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.squareup.picasso.Picasso
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.helpers.extensions.convertIntToDp
import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.models.MainTopicItem
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

        val view = LayoutInflater.from(this).inflate(R.layout.item_feature_item, null, false)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(20.convertIntToDp(this), 10.convertIntToDp(this), 20.convertIntToDp(this), 10.convertIntToDp(this))
        view.layoutParams = layoutParams

        sumarioListContainer.addView(view)

        val view2 = LayoutInflater.from(this).inflate(R.layout.item_feature_item, null, false)
        view2.layoutParams = layoutParams

        sumarioListContainer.addView(view2)

        val view3 = LayoutInflater.from(this).inflate(R.layout.item_feature_item, null, false)
        view3.layoutParams = layoutParams
        sumarioListContainer.addView(view3)

        val view4 = LayoutInflater.from(this).inflate(R.layout.item_feature_item, null, false)
        view4.layoutParams = layoutParams
        sumarioListContainer.addView(view4)


        val view5 = LayoutInflater.from(this).inflate(R.layout.item_feature_item, null, false)
        view4.layoutParams = layoutParams
        sumarioListContainer.addView(view5)


        val view6 = LayoutInflater.from(this).inflate(R.layout.item_feature_item, null, false)
        view4.layoutParams = layoutParams
        sumarioListContainer.addView(view6)

//        startButton.setTextColor(resources.getColor(android.R.color.white))
//
//        sumarioList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        sumarioList.adapter = SumarioAdapter(this, listOf(Sumario("", "", listOf()), Sumario("", "", listOf()), Sumario("", "", listOf()), Sumario("", "", listOf()), Sumario("", "", listOf())))
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