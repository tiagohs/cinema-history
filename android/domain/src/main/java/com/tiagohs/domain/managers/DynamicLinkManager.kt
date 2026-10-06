package com.tiagohs.domain.managers

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.tiagohs.entities.enums.ShareScreenTypeEnum
import com.tiagohs.helpers.Constants
import javax.inject.Inject

class DynamicLinkManager
@Inject constructor(
    val context: Context
) {

    /**
     * Firebase Dynamic Links foi desativado pelo Google em 25/08/2025.
     * Os links agora são App Links simples (https://thshoc.link/?screen=...),
     * lidos diretamente do Intent.
     */
    fun findScreenFromLink(
        activity: Activity,
        onComplete: (screenType: ShareScreenTypeEnum, deepLink: Uri) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        val deepLink = activity.intent?.data

        if (deepLink == null) {
            onError.invoke(Exception("Error to Get Screen Type"))
            return
        }

        try {
            val screenType = ShareScreenTypeEnum.getContentType(
                deepLink.getQueryParameter(Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.SCREEN)
            )
            onComplete.invoke(screenType, deepLink)
        } catch (ex: Exception) {
            onError.invoke(ex)
        }
    }

    fun buildHistoryCinemaPageLink(
        mainTopicId: Int,
        itemSelectedPosition: Int,
        onComplete: (link: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        val baseUrl = buildUrl(
            mapOf(
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.SCREEN to ShareScreenTypeEnum.HISTORY_PAGE.screenName,
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.MAIN_TOPIC_ID to mainTopicId.toString(),
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.HISTORY_PAGE_POSITION to itemSelectedPosition.toString()
            )
        )

        buildDynamicLink(baseUrl, onComplete, onError)
    }

    fun buildTimelinePageLink(
        timelineIndex: Int,
        onComplete: (link: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        val baseUrl = buildUrl(
            mapOf(
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.SCREEN to ShareScreenTypeEnum.TIMELINE_PAGE.screenName,
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.TIMELINE_INDEX to timelineIndex.toString()
            )
        )

        buildDynamicLink(baseUrl, onComplete, onError)
    }

    fun buildPersonPageLink(
        personId: Int,
        onComplete: (link: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        val baseUrl = buildUrl(
            mapOf(
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.SCREEN to ShareScreenTypeEnum.PERSON_PAGE.screenName,
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.PERSON_ID to personId.toString()
            )
        )

        buildDynamicLink(baseUrl, onComplete, onError)
    }

    fun buildMoviePageLink(
        movieid: Int,
        onComplete: (link: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        val baseUrl = buildUrl(
            mapOf(
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.SCREEN to ShareScreenTypeEnum.MOVIE_PAGE.screenName,
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.MOVIE_ID to movieid.toString()
            )
        )

        buildDynamicLink(baseUrl, onComplete, onError)
    }

    fun buildAwardPageLink(
        awardId: Int,
        onComplete: (link: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        val baseUrl = buildUrl(
            mapOf(
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.SCREEN to ShareScreenTypeEnum.AWARD_PAGE.screenName,
                Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.AWARD_ID to awardId.toString()
            )
        )

        buildDynamicLink(baseUrl, onComplete, onError)
    }


    private fun buildDynamicLink(
        buildBaseUrl: String,
        onComplete: (link: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        try {
            onComplete.invoke(Uri.parse(buildBaseUrl).toString())
        } catch (ex: Exception) {
            onError.invoke(ex)
        }
    }

    private fun buildUrl(parameters: Map<String, String>): String {
        var baseUrl = Constants.FIREBASE.BASE_URL

        parameters.entries.forEachIndexed { index, map ->
            if (index == 0) {
                baseUrl += "?${map.key}=${map.value}&"
            } else if (index == parameters.size - 1) {
                baseUrl += "${map.key}=${map.value}"
            } else {
                baseUrl += "${map.key}=${map.value}&"
            }
        }

        return baseUrl
    }
}