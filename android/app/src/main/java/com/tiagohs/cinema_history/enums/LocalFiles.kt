package com.tiagohs.cinema_history.enums

import okhttp3.Request

enum class LocalFiles(
    var path: String,
    var raw: String
) {
    MAIN_TOPICS("/maintopics", "local/maintopics.json"),
    PAGE_1("/page1", "local/page1.json");

    fun isValid(request: Request): Boolean = request.url.toUri().path.contains(path, true)
}