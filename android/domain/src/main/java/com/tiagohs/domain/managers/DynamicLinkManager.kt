package com.tiagohs.domain.managers

import android.content.Context
import javax.inject.Inject

class DynamicLinkManager
@Inject constructor(
    val context: Context
) {

    fun buildUrl(parameters: Map<String, String>): String {
        var baseUrl = "https://thshoc.link/"

        parameters.entries.forEach { map ->
            baseUrl += "${map.key}=${map.value}"
        }

        return baseUrl
    }
}