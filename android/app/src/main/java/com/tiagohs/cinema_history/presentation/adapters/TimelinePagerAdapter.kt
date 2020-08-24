package com.tiagohs.cinema_history.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tiagohs.cinema_history.presentation.fragments.TimelineFragment

class TimelinePagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    val timelineIds: List<Int>
): FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = timelineIds.size

    override fun createFragment(position: Int): Fragment = TimelineFragment.newInstance(timelineIds[position], timelineIds.size)
}