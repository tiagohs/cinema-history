package com.tiagohs.cinema_history.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tiagohs.cinema_history.presentation.fragments.HistoryPageFragment
import com.tiagohs.entities.Sumario

class PagePagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private val listOfSumario: List<Sumario>
): FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = listOfSumario.size

    override fun createFragment(position: Int): Fragment = HistoryPageFragment.newInstance(listOfSumario[position])
}