package com.tiagohs.entities.references

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.Quote
import com.tiagohs.entities.image.Image
import java.io.Serializable

class ReferenceText(
    @SerializedName("text")
    var text: String
): Reference(), Serializable