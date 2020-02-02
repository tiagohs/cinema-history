package com.tiagohs.domain.services.deserializers

import com.google.gson.*
import com.tiagohs.entities.timeline.*
import com.tiagohs.entities.enums.TimelineType
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
            TimelineType.ITEM -> Gson().fromJson(obj, TimelineItem::class.java)
            TimelineType.FOOTER -> Gson().fromJson(obj, TimelineFooter::class.java)
        }
    }
}