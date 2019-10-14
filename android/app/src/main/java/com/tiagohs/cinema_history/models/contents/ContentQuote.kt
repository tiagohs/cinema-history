package com.tiagohs.cinema_history.models.contents

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.Quote
import java.io.Serializable

data class ContentQuote(

    @SerializedName("quote")
    var quote: Quote,

    @SerializedName("quote_mark_color")
    var quoteMarkColor: String?
): Content(), Serializable