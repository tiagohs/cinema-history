package com.tiagohs.entities.references

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ReferenceResult(
    @SerializedName("name")
    var name: String? = null,

    var references: List<Reference>? = null
) : Serializable