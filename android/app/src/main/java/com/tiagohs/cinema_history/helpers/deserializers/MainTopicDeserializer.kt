package com.tiagohs.cinema_history.helpers.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tiagohs.cinema_history.enums.ContentType
import com.tiagohs.cinema_history.enums.MainTopicItemLayoutType
import com.tiagohs.cinema_history.models.MainTopic
import com.tiagohs.cinema_history.models.MainTopicItem
import com.tiagohs.cinema_history.models.Quote
import java.lang.reflect.Type

class MainTopicDeserializer: JsonDeserializer<MainTopic> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MainTopic {
        val obj = json.asJsonObject
        val type = MainTopicItemLayoutType.getContentType(obj.get("layout_type").asString)

        return when (type) {
            MainTopicItemLayoutType.QUOTE -> Gson().fromJson(obj, Quote::class.java)
            else -> Gson().fromJson(obj, MainTopicItem::class.java)
        }
    }
}