package com.tiagohs.entities.references

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.Quote
import java.io.Serializable

class ReferenceBook(
    @SerializedName("quote")
    var quote: Quote,

    @SerializedName("quote_mark_color")
    var quoteMarkColor: String?
): Reference(), Serializable