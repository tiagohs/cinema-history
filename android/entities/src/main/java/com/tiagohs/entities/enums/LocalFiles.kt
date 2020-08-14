package com.tiagohs.entities.enums

import okhttp3.Request

enum class LocalFiles(
    var path: String,
    var raw: String
) {
    TIMELINE_LIST("/timelines", "local/timelines/timeline_list.json"),
    TIMELINE_1("/timeline_1", "local/timelines/timeline_1.json"),
    TIMELINE_2("/timeline_2", "local/timelines/timeline_2.json"),

    MAIN_TOPICS("/maintopics", "local/maintopics.json"),
    MIL_MOVIES_MAIN_TOPICS("/milmoviesmaintopics", "local/milmoviesmaintopics.json"),
    DIRECTORS_MAIN_TOPICS("/directorsmaintopics", "local/directorsmaintopics.json"),

    PERSON_SPECIALS("/special_persons", "local/specials/persons.json"),
    MOVIE_SPECIALS("/special_movies", "local/specials/movies.json"),

    HISTORY_SUMARIO_1("/hmt_sumario_1", "local/history_sumarios/hmt_sumarios_1.json"),

    // Main Topic 1900 to 1929
    MAIN_1_PAGE_1("/main_1/page_1", "local/pages/main_1/page_1.json"),
    MAIN_1_PAGE_2("/main_1/page_2", "local/pages/main_1/page_2.json"),
    MAIN_1_PAGE_3("/main_1/page_3", "local/pages/main_1/page_3.json"),
    MAIN_1_PAGE_4("/main_1/page_4", "local/pages/main_1/page_4.json"),
    MAIN_1_PAGE_5("/main_1/page_5", "local/pages/main_1/page_5.json"),
    MAIN_1_PAGE_6("/main_1/page_6", "local/pages/main_1/page_6.json"),
    MAIN_1_PAGE_7("/main_1/page_7", "local/pages/main_1/page_7.json"),
    MAIN_1_PAGE_8("/main_1/page_8", "local/pages/main_1/page_8.json");

    fun isValid(request: Request): Boolean = request.url.toUri().path.contains(path, true)
}