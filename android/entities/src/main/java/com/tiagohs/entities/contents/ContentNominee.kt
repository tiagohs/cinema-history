package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.awards.Nominee
import java.io.Serializable

data class ContentNominee(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("year")
    val year: String? = null,

    @SerializedName("nominee_list")
    val nomineeList: List<Nominee>? = null
) : Content(), Serializable