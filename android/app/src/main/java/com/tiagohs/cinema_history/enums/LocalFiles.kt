package com.tiagohs.cinema_history.enums

import okhttp3.Request

enum class LocalFiles(
    var path: String,
    var raw: String
) {
    MAIN_TOPICS("/maintopics", "local/maintopics.json"),
    MIL_MOVIES_MAIN_TOPICS("/milmoviesmaintopics", "local/milmoviesmaintopics.json"),

    HISTORY_SUMARIO_1("/hmt_sumario_1", "local/history_sumarios/hmt_sumarios_1.json"),

    // Main Topic 1900 to 1929
    MAIN_1_PAGE_1("/main_1/page_1", "local/pages/main_1/page_1.json"),
    MAIN_1_PAGE_2("/main_1/page_2", "local/pages/main_1/page_2.json");

    fun isValid(request: Request): Boolean = request.url.toUri().path.contains(path, true)
}