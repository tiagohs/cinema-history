package com.tiagohs.cinema_history.models.timeline

import java.io.Serializable

data class TimelineItem(
    val header: String,
    val content: String
): Serializable