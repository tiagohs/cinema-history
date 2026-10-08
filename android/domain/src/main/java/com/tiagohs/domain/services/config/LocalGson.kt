package com.tiagohs.domain.services.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tiagohs.domain.services.deserializers.MainTopicDeserializer
import com.tiagohs.domain.services.deserializers.PageContentDeserializer
import com.tiagohs.domain.services.deserializers.ReferencesDeserializer
import com.tiagohs.domain.services.deserializers.TimelineDeserializer
import com.tiagohs.entities.contents.Content
import com.tiagohs.entities.main_topics.MainTopic
import com.tiagohs.entities.references.Reference
import com.tiagohs.entities.timeline.Timeline

/**
 * Gson usado para ler o conteúdo (assets/local e APIs).
 * Fica isolado aqui para que o teste de conteúdo (LocalContentTest) use exatamente a mesma configuração do app.
 */
object LocalGson {

    fun create(): Gson = GsonBuilder()
        .registerTypeAdapter(Content::class.java, PageContentDeserializer())
        .registerTypeAdapter(MainTopic::class.java, MainTopicDeserializer())
        .registerTypeAdapter(Timeline::class.java, TimelineDeserializer())
        .registerTypeAdapter(Reference::class.java, ReferencesDeserializer())
        .create()
}
