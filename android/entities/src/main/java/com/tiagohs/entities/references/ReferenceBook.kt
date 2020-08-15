package com.tiagohs.entities.references

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.Quote
import com.tiagohs.entities.image.Image
import java.io.Serializable

class ReferenceBook(
    @SerializedName("title")
    var title: String,

    @SerializedName("subtitle")
    var subtitle: String,

    @SerializedName("description")
    var description: String,

    @SerializedName("image")
    var image: Image,

    @SerializedName("mediaType")
    var mediaType: String,

    @SerializedName("link")
    var link: String
): Reference(), Serializable