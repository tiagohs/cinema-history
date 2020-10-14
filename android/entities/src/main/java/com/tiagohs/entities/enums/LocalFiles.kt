package com.tiagohs.entities.enums

import okhttp3.Request

enum class LocalFiles(
    var path: String,
    var raw: String
) {
    HOME_CONTENT("/homecontent", "local/homecontent.json"),
    REFERENCES("/references", "local/references.json"),
    GLOSSARY("/glossary", "local/glossary.json"),

    TIMELINE_LIST("/timelines", "local/timelines/timeline_list.json"),
    TIMELINE_1("/timeline_1", "local/timelines/timeline_1.json"),
    TIMELINE_2("/timeline_2", "local/timelines/timeline_2.json"),
    TIMELINE_3("/timeline_3", "local/timelines/timeline_3.json"),
    TIMELINE_4("/timeline_4", "local/timelines/timeline_4.json"),

    AWARDS_NOMINEES_1("/awards/nominees/1", "local/awards/nominees/1.json"),
    AWARDS_HISTORY_1("/awards/history/1", "local/awards/history/1.json"),
    AWARDS_NOMINEES_2("/awards/nominees/2", "local/awards/nominees/2.json"),
    AWARDS_HISTORY_2("/awards/history/2", "local/awards/history/2.json"),
    AWARDS_NOMINEES_3("/awards/nominees/3", "local/awards/nominees/3.json"),
    AWARDS_HISTORY_3("/awards/history/3", "local/awards/history/3.json"),
    AWARDS_NOMINEES_4("/awards/nominees/4", "local/awards/nominees/4.json"),
    AWARDS_HISTORY_4("/awards/history/4", "local/awards/history/4.json"),
    AWARDS_NOMINEES_5("/awards/nominees/5", "local/awards/nominees/5.json"),
    AWARDS_HISTORY_5("/awards/history/5", "local/awards/history/5.json"),
    AWARDS_NOMINEES_6("/awards/nominees/6", "local/awards/nominees/6.json"),
    AWARDS_HISTORY_6("/awards/history/6", "local/awards/history/6.json"),
    AWARDS_NOMINEES_7("/awards/nominees/7", "local/awards/nominees/7.json"),
    AWARDS_HISTORY_7("/awards/history/7", "local/awards/history/7.json"),

    MAIN_TOPICS("/maintopics", "local/maintopics.json"),
    MIL_MOVIES_MAIN_TOPICS("/milmoviesmaintopics", "local/milmoviesmaintopics.json"),
    DIRECTORS_MAIN_TOPICS("/directorsmaintopics", "local/directorsmaintopics.json"),
    AWARDS_MAIN_TOPICS("/awards", "local/awards.json"),

    PERSON_SPECIALS("/special_persons", "local/specials/persons.json"),
    MOVIE_SPECIALS("/special_movies", "local/specials/movies.json"),

    HISTORY_SUMARIO_1("/hmt_sumario_1", "local/history_sumarios/hmt_sumarios_1.json"),
    HISTORY_SUMARIO_2("/hmt_sumario_2", "local/history_sumarios/hmt_sumarios_2.json"),
    HISTORY_SUMARIO_3("/hmt_sumario_3", "local/history_sumarios/hmt_sumarios_3.json"),
    HISTORY_SUMARIO_4("/hmt_sumario_4", "local/history_sumarios/hmt_sumarios_4.json"),

    // Main Topic 1985 to 1929
    MAIN_1_PAGE_1("/main_1/page_1", "local/pages/main_1/page_1.json"),
    MAIN_1_PAGE_2("/main_1/page_2", "local/pages/main_1/page_2.json"),
    MAIN_1_PAGE_3("/main_1/page_3", "local/pages/main_1/page_3.json"),
    MAIN_1_PAGE_4("/main_1/page_4", "local/pages/main_1/page_4.json"),
    MAIN_1_PAGE_5("/main_1/page_5", "local/pages/main_1/page_5.json"),
    MAIN_1_PAGE_6("/main_1/page_6", "local/pages/main_1/page_6.json"),
    MAIN_1_PAGE_7("/main_1/page_7", "local/pages/main_1/page_7.json"),
    MAIN_1_PAGE_8("/main_1/page_8", "local/pages/main_1/page_8.json"),
    MAIN_1_PAGE_9("/main_1/page_9", "local/pages/main_1/page_9.json"),

    // Main Topic 1930 to 1939
    MAIN_2_PAGE_1("/main_2/page_1", "local/pages/main_2/page_1.json"),
    MAIN_2_PAGE_2("/main_2/page_2", "local/pages/main_2/page_2.json"),
    MAIN_2_PAGE_3("/main_2/page_3", "local/pages/main_2/page_3.json"),
    MAIN_2_PAGE_4("/main_2/page_4", "local/pages/main_2/page_4.json"),
    MAIN_2_PAGE_5("/main_2/page_5", "local/pages/main_2/page_5.json"),
    MAIN_2_PAGE_6("/main_2/page_6", "local/pages/main_2/page_6.json"),
    MAIN_2_PAGE_7("/main_2/page_7", "local/pages/main_2/page_7.json"),
    MAIN_2_PAGE_8("/main_2/page_8", "local/pages/main_2/page_8.json"),
    MAIN_2_PAGE_9("/main_2/page_9", "local/pages/main_2/page_9.json"),

    // Main Topic 1940 a 1959
    MAIN_3_PAGE_1("/main_3/page_1", "local/pages/main_3/page_1.json"),
    MAIN_3_PAGE_2("/main_3/page_2", "local/pages/main_3/page_2.json"),
    MAIN_3_PAGE_3("/main_3/page_3", "local/pages/main_3/page_3.json"),
    MAIN_3_PAGE_4("/main_3/page_4", "local/pages/main_3/page_4.json"),
    MAIN_3_PAGE_5("/main_3/page_5", "local/pages/main_3/page_5.json"),
    MAIN_3_PAGE_6("/main_3/page_6", "local/pages/main_3/page_6.json"),
    MAIN_3_PAGE_7("/main_3/page_7", "local/pages/main_3/page_7.json"),
    MAIN_3_PAGE_8("/main_3/page_8", "local/pages/main_3/page_8.json"),
    MAIN_3_PAGE_9("/main_3/page_9", "local/pages/main_3/page_9.json"),
    MAIN_3_PAGE_10("/main_3/page_10", "local/pages/main_3/page_10.json"),
    MAIN_3_PAGE_11("/main_3/page_11", "local/pages/main_3/page_11.json"),

    // Main Topic 1960 a 1969
    MAIN_4_PAGE_1("/main_4/page_1", "local/pages/main_4/page_1.json"),
    MAIN_4_PAGE_2("/main_4/page_2", "local/pages/main_4/page_2.json"),
    MAIN_4_PAGE_3("/main_4/page_3", "local/pages/main_4/page_3.json"),
    MAIN_4_PAGE_4("/main_4/page_4", "local/pages/main_4/page_4.json"),
    MAIN_4_PAGE_5("/main_4/page_5", "local/pages/main_4/page_5.json"),
    MAIN_4_PAGE_6("/main_4/page_6", "local/pages/main_4/page_6.json"),
    MAIN_4_PAGE_7("/main_4/page_7", "local/pages/main_4/page_7.json"),
    MAIN_4_PAGE_8("/main_4/page_8", "local/pages/main_4/page_8.json");

    fun isValid(request: Request): Boolean = request.url.toUri().path == path
}