package com.tiagohs.entities.enums

import com.tiagohs.entities.R
import java.io.Serializable

enum class AwardsPageType(
    val screenName: Int
): Serializable {
    HISTORY(R.string.award_history),
    NOMINEES(R.string.award_nominees);
}