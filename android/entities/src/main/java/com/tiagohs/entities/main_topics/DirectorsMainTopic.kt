package com.tiagohs.entities.main_topics

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.image.Image
import java.io.Serializable

class DirectorsMainTopic(
    @SerializedName("person_id")
    val personId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("image")
    val image: Image,

    /** Período de vida, ex.: "1910–1998" ou "n. 1969". */
    @SerializedName("years")
    val years: String? = null,

    @SerializedName("country")
    val country: String? = null,

    /** Frase curta do cartão. */
    @SerializedName("description")
    val description: String? = null,

    /** Época principal da obra: silent, golden, new_waves, modern, contemporary. */
    @SerializedName("era")
    val era: String? = null,

    /** Região: europe, north_america, latin_america, asia, africa, oceania. */
    @SerializedName("region")
    val region: String? = null,

    /** Posição na seção "Em alta" (1, 2, 3...); 0 = fora da seção. */
    @SerializedName("trending")
    val trending: Int = 0
): MainTopic() , Serializable
