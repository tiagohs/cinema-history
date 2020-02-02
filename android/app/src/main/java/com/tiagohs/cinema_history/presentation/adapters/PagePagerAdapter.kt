package com.tiagohs.cinema_history.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tiagohs.cinema_history.presentation.fragments.HistoryPageFragment

class PagePagerAdapter(
    val fm: FragmentManager,
    val lifecycle: Lifecycle,
    val listOfSumario: List<com.tiagohs.entities.Sumario>
): FragmentStateAdapter(fm, lifecycle) {

    private var listOfFragments: HashMap<Int, Fragment> = HashMap()

    override fun getItemCount(): Int = listOfSumario.size

    override fun createFragment(position: Int): Fragment {
        val sumario = listOfSumario[position]

        return HistoryPageFragment.newInstance(sumario)
    }
}