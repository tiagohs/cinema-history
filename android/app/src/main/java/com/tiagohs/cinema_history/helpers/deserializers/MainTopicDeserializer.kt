package com.tiagohs.cinema_history.helpers.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tiagohs.cinema_history.enums.MainTopicItemLayoutType
import com.tiagohs.cinema_history.enums.MainTopicsType
import com.tiagohs.cinema_history.models.main_topics.MainTopic
import com.tiagohs.cinema_history.models.main_topics.MainTopicItem
import com.tiagohs.cinema_history.models.Quote
import com.tiagohs.cinema_history.models.main_topics.DirectorsMainTopic
import com.tiagohs.cinema_history.models.main_topics.MilMoviesMainTopic
import java.lang.reflect.Type

class MainTopicDeserializer: JsonDeserializer<MainTopic> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MainTopic {
        val obj = json.asJsonObject
        val layoutType = MainTopicItemLayoutType.getContentType(obj.get("layout_type").asString)
        val type = MainTopicsType.getContentType(obj.get("main_topic_type").asString)

        return when (layoutType) {
            MainTopicItemLayoutType.QUOTE -> Gson().fromJson(obj, Quote::class.java)
            else -> {

                when(type) {
                    MainTopicsType.HISTORY_CINEMA -> Gson().fromJson(obj, MainTopicItem::class.java)
                    MainTopicsType.MIL_MOVIES -> Gson().fromJson(obj, MilMoviesMainTopic::class.java)
                    MainTopicsType.DIRECTORS -> Gson().fromJson(obj, DirectorsMainTopic::class.java)
                    else -> Gson().fromJson(obj, MainTopicItem::class.java)
                }

            }
        }
    }
}