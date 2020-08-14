package com.tiagohs.entities.references

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.ContentType
import com.tiagohs.entities.enums.ReferenceType
import java.io.Serializable

open class Reference: Serializable {

    @SerializedName("type")
    var type: ReferenceType = ReferenceType.BOOK
}