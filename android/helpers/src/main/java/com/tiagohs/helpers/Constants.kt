package com.tiagohs.helpers

object Constants {
    const val APPLICATION_ID                    = "com.tiagohs.cinema_history"
    const val IS_FROM_UNIVERSAL_LINK = "IS_FROM_UNIVERSAL_LINK"

    object FIREBASE {
        const val BASE_URL                      = "https://thshoc.link/"
        const val DOMAIN_URL                    = "https://cinemahistory.page.link"

        object DYNAMIC_LINK_PARAMETERS_KEY {
            const val SCREEN                    = "screen"
            const val MAIN_TOPIC_ID             = "mainTopicId"
            const val HISTORY_PAGE_POSITION     = "historyPagePosition"
            const val PERSON_ID                 = "personId"
            const val MOVIE_ID                  = "movieId"
            const val AWARD_ID                  = "movieId"
            const val TIMELINE_INDEX            = "timelineIndex"
        }
    }

    object SHARED_PREFERENCES {

        const val DEFAULT                       = "AppPreference"

        const val PRIVATE_MODE                  = 0
        const val PREF_SETTINGS_NAME            = "settings"
    }

    object FONTS {
        const val FONT_EXTENSION                = ".otf"
    }

}