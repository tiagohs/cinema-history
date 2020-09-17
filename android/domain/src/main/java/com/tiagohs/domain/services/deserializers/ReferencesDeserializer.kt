package com.tiagohs.domain.services.deserializers

import com.google.gson.*
import com.tiagohs.entities.enums.ReferenceType
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.references.ReferenceMedia
import com.tiagohs.entities.references.ReferenceText
import java.lang.reflect.Type

class ReferencesDeserializer: JsonDeserializer<Reference> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Reference {
        val obj = json.asJsonObject
        val type = ReferenceType.getContentType(obj.get("type").asString)

        return when (type) {
            ReferenceType.MEDIA -> Gson().fromJson(obj, ReferenceMedia::class.java)
            ReferenceType.TEXT -> Gson().fromJson(obj, ReferenceText::class.java)
        }
    }
}