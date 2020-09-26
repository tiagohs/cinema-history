package com.tiagohs.entities.awards

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.NomineeType
import java.io.Serializable

data class Nominee(
    @SerializedName("type")
    val type: NomineeType,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("image_path")
    val imagePath: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("winner")
    val winner: Boolean? = null
) : Serializable