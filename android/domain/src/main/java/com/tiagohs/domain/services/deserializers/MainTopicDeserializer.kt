package com.tiagohs.domain.services.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tiagohs.entities.Quote
import com.tiagohs.entities.main_topics.DirectorsMainTopic
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.main_topics.MainTopicItem
import com.tiagohs.entities.main_topics.MilMoviesMainTopic
import com.tiagohs.entities.enums.MainTopicItemLayoutType
import com.tiagohs.entities.enums.MainTopicsType
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
            MainTopicItemLayoutType.QUOTE -> Gson().fromJson(obj, com.tiagohs.entities.Quote::class.java)
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