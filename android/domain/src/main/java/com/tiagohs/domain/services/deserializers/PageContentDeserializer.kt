package com.tiagohs.domain.services.deserializers

import com.google.gson.*
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.contents.*
import com.tiagohs.entities.contents.ContentText
import com.tiagohs.entities.enums.ContentType
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
            ContentType.SLIDE -> Gson().fromJson(obj, ContentSlide::class.java)
            ContentType.IMAGE -> Gson().fromJson(obj, ContentImage::class.java)
            ContentType.QUOTE -> Gson().fromJson(obj, ContentQuote::class.java)
            ContentType.AUDIO_STREAM -> Gson().fromJson(obj, ContentAudioStream::class.java)
            ContentType.VIDEO -> Gson().fromJson(obj, ContentVideo::class.java)
        }
    }
}