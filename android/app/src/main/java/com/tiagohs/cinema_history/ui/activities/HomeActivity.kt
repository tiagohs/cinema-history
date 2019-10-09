package com.tiagohs.cinema_history.ui.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.presenter.HomePresenter
import com.tiagohs.cinema_history.ui.adapters.MainTopicsAdapter
import com.tiagohs.cinema_history.ui.views.HomeView
import com.tiagohs.cinema_history.ui.configs.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity :
    BaseActivity(),
    HomeView {

    override fun onGetLayoutViewId(): Int = R.layout.activity_home
    override fun onGetMenuLayoutId(): Int = 0

    @Inject
    lateinit var homePresenter: HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getApplicationComponent()?.inject(this)

        homePresenter.onBindView(this)
        homePresenter.fetchMainTopics()
    }

    override fun onDestroy() {
        super.onDestroy()

        homePresenter.onUnbindView()
    }

    override fun bindMainTopics(mainTopics: List<MainTopic>) {
        mainTopicsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mainTopicsRecyclerView.adapter = MainTopicsAdapter(this, mainTopics)
    }

}
