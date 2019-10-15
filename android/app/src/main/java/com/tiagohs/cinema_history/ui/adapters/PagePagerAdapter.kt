package com.tiagohs.cinema_history.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tiagohs.cinema_history.models.Sumario
import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.ui.fragments.HistoryPageFragment

class PagePagerAdapter(
    val fm: FragmentManager,
    val lifecycle: Lifecycle,
    val listOfSumario: List<Sumario>
): FragmentStateAdapter(fm, lifecycle) {

    private var listOfFragments: HashMap<Int, Fragment> = HashMap()

    override fun getItemCount(): Int = listOfSumario.size

    override fun createFragment(position: Int): Fragment {
        val sumario = listOfSumario[position]

        return HistoryPageFragment.newInstance(sumario)
    }
}