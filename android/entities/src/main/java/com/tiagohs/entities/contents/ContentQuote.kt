package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.Quote
import java.io.Serializable

data class ContentQuote(

    @SerializedName("quote")
    var quote: Quote,

    @SerializedName("quote_mark_color")
    var quoteMarkColor: String? = null
): Content(), Serializable