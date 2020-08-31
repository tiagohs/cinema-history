package com.tiagohs.domain.managers

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.tiagohs.entities.enums.ShareScreenTypeEnum
import com.tiagohs.helpers.Constants
import javax.inject.Inject

class DynamicLinkManager
@Inject constructor(
    val context: Context
) {

    fun findScreenFromLink(
        activity: Activity,
        onComplete: (screenType: ShareScreenTypeEnum, deepLink: Uri) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(activity.intent)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    val deepLink = it.result?.link

                    if (deepLink != null) {
                        val screenType =
                            ShareScreenTypeEnum.getContentType(deepLink.getQueryParameter(Constants.FIREBASE.DYNAMIC_LINK_PARAMETERS_KEY.SCREEN))

                        onComplete.invoke(screenType, deepLink)
                        return@addOnCompleteListener
                    }
                }

                onError.invoke(Exception("Error to Get Screen Type"))
            }
            .addOnFailureListener { onError.invoke(it) }
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

    private fun buildDynamicLink(
        buildBaseUrl: String,
        onComplete: (link: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        val baseUrl = Uri.parse(buildBaseUrl)

        FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setLink(baseUrl)
            .setDomainUriPrefix(Constants.FIREBASE.DOMAIN_URL)
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder(Constants.APPLICATION_ID).build()
            )
            .buildShortDynamicLink()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val shortLink = it.result?.shortLink?.toString()

                    if (shortLink != null) {
                        onComplete.invoke(shortLink)
                        return@addOnCompleteListener
                    }
                }

                onError.invoke(Exception("Error to build URL"))
            }
            .addOnFailureListener { onError.invoke(it) }
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