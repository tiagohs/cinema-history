package com.tiagohs.entities.awards

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.NomineeType
import java.io.Serializable

class Nominee(
    @SerializedName("type")
    val type: NomineeType = NomineeType.MOVIE,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("image_path")
    val imagePath: String? = null,

    @SerializedName("winner")
    val winner: Boolean? = null,

    @SerializedName("movie")
    val movie: Nominee? = null,

    @SerializedName("department")
    val department: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("director")
    val director: String? = null
) : Serializable