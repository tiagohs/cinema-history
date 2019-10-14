package com.tiagohs.cinema_history.helpers.deserializers

import com.google.gson.*
import com.tiagohs.cinema_history.enums.ContentType
import com.tiagohs.cinema_history.models.contents.Content
import com.tiagohs.cinema_history.models.contents.ContentGif
import com.tiagohs.cinema_history.models.contents.ContentText
import java.lang.reflect.Type

class PageContentDeserializer: JsonDeserializer<Content> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Content {
        val obj = json.asJsonObject
        val type = ContentType.getContentType(obj.get("type").asString)

        return when (type) {
            ContentType.TEXT -> Gson().fromJson(obj, ContentText::class.java)
            ContentType.GIF -> Gson().fromJson(obj, ContentGif::class.java)
            else -> { Gson().fromJson(obj, Content::class.java) }
        }
    }
}