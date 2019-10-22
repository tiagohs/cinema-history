package com.tiagohs.cinema_history.helpers.deserializers

import com.google.gson.*
import com.tiagohs.cinema_history.enums.ContentType
import com.tiagohs.cinema_history.enums.TimelineType
import com.tiagohs.cinema_history.models.contents.*
import com.tiagohs.cinema_history.models.timeline.Timeline
import com.tiagohs.cinema_history.models.timeline.TimelineItem
import com.tiagohs.cinema_history.models.timeline.TimelineTitle
import java.lang.reflect.Type

class TimelineDeserializer: JsonDeserializer<Timeline> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Timeline {
        val obj = json.asJsonObject
        val type = TimelineType.getContentType(obj.get("type").asString)

        return when (type) {
            TimelineType.TITLE -> Gson().fromJson(obj, TimelineTitle::class.java)
            TimelineType.RIGHT -> Gson().fromJson(obj, TimelineItem::class.java)
            TimelineType.LEFT -> Gson().fromJson(obj, TimelineItem::class.java)
        }
    }
}