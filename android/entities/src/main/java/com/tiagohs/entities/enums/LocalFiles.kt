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
    TIMELINE_5("/timeline_5", "local/timelines/timeline_5.json"),
    TIMELINE_6("/timeline_6", "local/timelines/timeline_6.json"),
    TIMELINE_7("/timeline_7", "local/timelines/timeline_7.json"),

    AWARDS_NOMINEES_1("/awards/nominees/1", "local/awards/nominees/nominees_1.json"),
    AWARDS_HISTORY_1("/awards/history/1", "local/awards/history/history1.json"),
    AWARDS_NOMINEES_2("/awards/nominees/2", "local/awards/nominees/nominees_2.json"),
    AWARDS_HISTORY_2("/awards/history/2", "local/awards/history/history2.json"),
    AWARDS_NOMINEES_3("/awards/nominees/3", "local/awards/nominees/nominees_3.json"),
    AWARDS_HISTORY_3("/awards/history/3", "local/awards/history/history3.json"),
    AWARDS_NOMINEES_4("/awards/nominees/4", "local/awards/nominees/nominees_4.json"),
    AWARDS_HISTORY_4("/awards/history/4", "local/awards/history/history4.json"),
    AWARDS_NOMINEES_5("/awards/nominees/5", "local/awards/nominees/nominees_5.json"),
    AWARDS_HISTORY_5("/awards/history/5", "local/awards/history/history5.json"),
    AWARDS_NOMINEES_6("/awards/nominees/6", "local/awards/nominees/nominees_6.json"),
    AWARDS_HISTORY_6("/awards/history/6", "local/awards/history/history_6.json"),
    AWARDS_NOMINEES_7("/awards/nominees/7", "local/awards/nominees/nominees_7.json"),
    AWARDS_HISTORY_7("/awards/history/7", "local/awards/history/history7.json"),

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
    HISTORY_SUMARIO_5("/hmt_sumario_5", "local/history_sumarios/hmt_sumarios_5.json"),
    HISTORY_SUMARIO_6("/hmt_sumario_6", "local/history_sumarios/hmt_sumarios_6.json"),
    HISTORY_SUMARIO_7("/hmt_sumario_7", "local/history_sumarios/hmt_sumarios_7.json"),

    // Main Topic 1985 to 1929
    MAIN_1_PAGE_1("/main_1/main_1_page_1", "local/pages/main_1/main_1_page_1.json"),
    MAIN_1_PAGE_2("/main_1/main_1_page_2", "local/pages/main_1/main_1_page_2.json"),
    MAIN_1_PAGE_3("/main_1/main_1_page_3", "local/pages/main_1/main_1_page_3.json"),
    MAIN_1_PAGE_4("/main_1/main_1_page_4", "local/pages/main_1/main_1_page_4.json"),
    MAIN_1_PAGE_5("/main_1/main_1_page_5", "local/pages/main_1/main_1_page_5.json"),
    MAIN_1_PAGE_6("/main_1/main_1_page_6", "local/pages/main_1/main_1_page_6.json"),
    MAIN_1_PAGE_7("/main_1/main_1_page_7", "local/pages/main_1/main_1_page_7.json"),
    MAIN_1_PAGE_8("/main_1/main_1_page_8", "local/pages/main_1/main_1_page_8.json"),
    MAIN_1_PAGE_9("/main_1/main_1_page_9", "local/pages/main_1/main_1_page_9.json"),

    // Main Topic 1930 to 1939
    MAIN_2_PAGE_1("/main_2/main_2_page_1", "local/pages/main_2/main_2_page_1.json"),
    MAIN_2_PAGE_2("/main_2/main_2_page_2", "local/pages/main_2/main_2_page_2.json"),
    MAIN_2_PAGE_3("/main_2/main_2_page_3", "local/pages/main_2/main_2_page_3.json"),
    MAIN_2_PAGE_4("/main_2/main_2_page_4", "local/pages/main_2/main_2_page_4.json"),
    MAIN_2_PAGE_5("/main_2/main_2_page_5", "local/pages/main_2/main_2_page_5.json"),
    MAIN_2_PAGE_6("/main_2/main_2_page_6", "local/pages/main_2/main_2_page_6.json"),
    MAIN_2_PAGE_7("/main_2/main_2_page_7", "local/pages/main_2/main_2_page_7.json"),
    MAIN_2_PAGE_8("/main_2/main_2_page_8", "local/pages/main_2/main_2_page_8.json"),
    MAIN_2_PAGE_9("/main_2/main_2_page_9", "local/pages/main_2/main_2_page_9.json"),

    // Main Topic 1940 a 1959
    MAIN_3_PAGE_1("/main_3/main_3_page_1", "local/pages/main_3/main_3_page_1.json"),
    MAIN_3_PAGE_2("/main_3/main_3_page_2", "local/pages/main_3/main_3_page_2.json"),
    MAIN_3_PAGE_3("/main_3/main_3_page_3", "local/pages/main_3/main_3_page_3.json"),
    MAIN_3_PAGE_4("/main_3/main_3_page_4", "local/pages/main_3/main_3_page_4.json"),
    MAIN_3_PAGE_5("/main_3/main_3_page_5", "local/pages/main_3/main_3_page_5.json"),
    MAIN_3_PAGE_6("/main_3/main_3_page_6", "local/pages/main_3/main_3_page_6.json"),
    MAIN_3_PAGE_7("/main_3/main_3_page_7", "local/pages/main_3/main_3_page_7.json"),
    MAIN_3_PAGE_8("/main_3/main_3_page_8", "local/pages/main_3/main_3_page_8.json"),
    MAIN_3_PAGE_9("/main_3/main_3_page_9", "local/pages/main_3/main_3_page_9.json"),
    MAIN_3_PAGE_10("/main_3/main_3_page_10", "local/pages/main_3/main_3_page_10.json"),
    MAIN_3_PAGE_11("/main_3/main_3_page_11", "local/pages/main_3/main_3_page_11.json"),

    // Main Topic 1960 a 1969
    MAIN_4_PAGE_1("/main_4/main_4_page_1", "local/pages/main_4/main_4_page_1.json"),
    MAIN_4_PAGE_2("/main_4/main_4_page_2", "local/pages/main_4/main_4_page_2.json"),
    MAIN_4_PAGE_3("/main_4/main_4_page_3", "local/pages/main_4/main_4_page_3.json"),
    MAIN_4_PAGE_4("/main_4/main_4_page_4", "local/pages/main_4/main_4_page_4.json"),
    MAIN_4_PAGE_5("/main_4/main_4_page_5", "local/pages/main_4/main_4_page_5.json"),
    MAIN_4_PAGE_6("/main_4/main_4_page_6", "local/pages/main_4/main_4_page_6.json"),
    MAIN_4_PAGE_7("/main_4/main_4_page_7", "local/pages/main_4/main_4_page_7.json"),
    MAIN_4_PAGE_8("/main_4/main_4_page_8", "local/pages/main_4/main_4_page_8.json"),


    // Main Topic 1970 a 1989
    MAIN_5_PAGE_1("/main_5/main_5_page_1", "local/pages/main_5/main_5_page_1.json"),
    MAIN_5_PAGE_2("/main_5/main_5_page_2", "local/pages/main_5/main_5_page_2.json"),
    MAIN_5_PAGE_3("/main_5/main_5_page_3", "local/pages/main_5/main_5_page_3.json"),
    MAIN_5_PAGE_4("/main_5/main_5_page_4", "local/pages/main_5/main_5_page_4.json"),
    MAIN_5_PAGE_5("/main_5/main_5_page_5", "local/pages/main_5/main_5_page_5.json"),
    MAIN_5_PAGE_6("/main_5/main_5_page_6", "local/pages/main_5/main_5_page_6.json"),
    MAIN_5_PAGE_7("/main_5/main_5_page_7", "local/pages/main_5/main_5_page_7.json"),
    MAIN_5_PAGE_8("/main_5/main_5_page_8", "local/pages/main_5/main_5_page_8.json"),
    MAIN_5_PAGE_9("/main_5/main_5_page_9", "local/pages/main_5/main_5_page_9.json"),
    MAIN_5_PAGE_10("/main_5/main_5_page_10", "local/pages/main_5/main_5_page_10.json"),
    MAIN_5_PAGE_11("/main_5/main_5_page_11", "local/pages/main_5/main_5_page_11.json"),
    MAIN_5_PAGE_12("/main_5/main_5_page_12", "local/pages/main_5/main_5_page_12.json"),
    MAIN_5_PAGE_13("/main_5/main_5_page_13", "local/pages/main_5/main_5_page_13.json"),
    MAIN_5_PAGE_14("/main_5/main_5_page_14", "local/pages/main_5/main_5_page_14.json"),
    MAIN_5_PAGE_15("/main_5/main_5_page_15", "local/pages/main_5/main_5_page_15.json"),
    MAIN_5_PAGE_16("/main_5/main_5_page_16", "local/pages/main_5/main_5_page_16.json"),
    MAIN_5_PAGE_17("/main_5/main_5_page_17", "local/pages/main_5/main_5_page_17.json"),
    MAIN_5_PAGE_18("/main_5/main_5_page_18", "local/pages/main_5/main_5_page_18.json"),


    // Main Topic 1990 a 2009
    MAIN_6_PAGE_1("/main_6/main_6_page_1", "local/pages/main_6/main_6_page_1.json"),
    MAIN_6_PAGE_2("/main_6/main_6_page_2", "local/pages/main_6/main_6_page_2.json"),
    MAIN_6_PAGE_3("/main_6/main_6_page_3", "local/pages/main_6/main_6_page_3.json"),
    MAIN_6_PAGE_4("/main_6/main_6_page_4", "local/pages/main_6/main_6_page_4.json"),
    MAIN_6_PAGE_5("/main_6/main_6_page_5", "local/pages/main_6/main_6_page_5.json"),
    MAIN_6_PAGE_6("/main_6/main_6_page_6", "local/pages/main_6/main_6_page_6.json"),
    MAIN_6_PAGE_7("/main_6/main_6_page_7", "local/pages/main_6/main_6_page_7.json"),
    MAIN_6_PAGE_8("/main_6/main_6_page_8", "local/pages/main_6/main_6_page_8.json"),
    MAIN_6_PAGE_9("/main_6/main_6_page_9", "local/pages/main_6/main_6_page_9.json"),
    MAIN_6_PAGE_10("/main_6/main_6_page_10", "local/pages/main_6/main_6_page_10.json"),
    MAIN_6_PAGE_11("/main_6/main_6_page_11", "local/pages/main_6/main_6_page_11.json"),
    MAIN_6_PAGE_12("/main_6/main_6_page_12", "local/pages/main_6/main_6_page_12.json"),
    MAIN_6_PAGE_13("/main_6/main_6_page_13", "local/pages/main_6/main_6_page_13.json"),
    MAIN_6_PAGE_14("/main_6/main_6_page_14", "local/pages/main_6/main_6_page_14.json"),
    MAIN_6_PAGE_15("/main_6/main_6_page_15", "local/pages/main_6/main_6_page_15.json"),
    MAIN_6_PAGE_16("/main_6/main_6_page_16", "local/pages/main_6/main_6_page_16.json"),
    MAIN_6_PAGE_17("/main_6/main_6_page_17", "local/pages/main_6/main_6_page_17.json"),
    MAIN_6_PAGE_18("/main_6/main_6_page_18", "local/pages/main_6/main_6_page_18.json"),
    MAIN_6_PAGE_19("/main_6/main_6_page_19", "local/pages/main_6/main_6_page_19.json"),

    // Main Topic 2010 a atualmente
    MAIN_7_PAGE_1("/main_7/main_7_page_1", "local/pages/main_7/main_7_page_1.json"),
    MAIN_7_PAGE_2("/main_7/main_7_page_2", "local/pages/main_7/main_7_page_2.json"),
    MAIN_7_PAGE_3("/main_7/main_7_page_3", "local/pages/main_7/main_7_page_3.json"),
    MAIN_7_PAGE_4("/main_7/main_7_page_4", "local/pages/main_7/main_7_page_4.json"),
    MAIN_7_PAGE_5("/main_7/main_7_page_5", "local/pages/main_7/main_7_page_5.json"),
    MAIN_7_PAGE_6("/main_7/main_7_page_6", "local/pages/main_7/main_7_page_6.json"),
    MAIN_7_PAGE_7("/main_7/main_7_page_7", "local/pages/main_7/main_7_page_7.json"),
    MAIN_7_PAGE_8("/main_7/main_7_page_8", "local/pages/main_7/main_7_page_8.json"),
    MAIN_7_PAGE_9("/main_7/main_7_page_9", "local/pages/main_7/main_7_page_9.json"),
    MAIN_7_PAGE_10("/main_7/main_7_page_10", "local/pages/main_7/main_7_page_10.json");

    fun isValid(request: Request): Boolean = request.url.toUri().path == path
}