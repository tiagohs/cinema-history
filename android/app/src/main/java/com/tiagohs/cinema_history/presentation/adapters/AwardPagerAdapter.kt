package com.tiagohs.cinema_history.presentation.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tiagohs.cinema_history.presentation.fragments.AwardsFragment
import com.tiagohs.cinema_history.presentation.fragments.AwardsNomineeFragment
import com.tiagohs.entities.enums.AwardsPageType
import com.tiagohs.entities.main_topics.AwardMainTopic
import com.tiagohs.helpers.extensions.getResourceString

class AwardPagerAdapter(
    val context: Context?,
    private val awardMainTopic: AwardMainTopic,
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = AwardsPageType.values().size

    override fun getItem(tabSelect: Int): Fragment = when (tabSelect) {
        AwardsPageType.HISTORY.ordinal -> AwardsFragment.newInstance(
            awardMainTopic,
            AwardsPageType.values()[tabSelect]
        )
        AwardsPageType.NOMINEES.ordinal -> AwardsNomineeFragment.newInstance(
            awardMainTopic,
            AwardsPageType.values()[tabSelect]
        )
        else -> Fragment()
    }

    override fun getPageTitle(position: Int): CharSequence =
        context.getResourceString(AwardsPageType.values()[position].screenName)

}