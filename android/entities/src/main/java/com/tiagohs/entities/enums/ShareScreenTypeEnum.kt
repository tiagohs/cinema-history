package com.tiagohs.entities.enums

enum class ShareScreenTypeEnum(
    val screenName: String
) {
    HISTORY_PAGE("historyPage"),
    MOVIE_PAGE("moviePage"),
    PERSON_PAGE("personPage"),
    TIMELINE_PAGE("timelinePage"),
    UNKNOWN("unknown");

    companion object {
        fun getContentType(type: String?): ShareScreenTypeEnum {
            var typeEnum = UNKNOWN

            for (typeValue in values()) {
                if (typeValue.screenName == type) {
                    typeEnum = typeValue
                    break
                }
            }

            return typeEnum
        }
    }
}