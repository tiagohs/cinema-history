package com.tiagohs.entities.contents

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ContentTwitter(

    @SerializedName("twitter_url")
    val twitterUrl: String,

    @SerializedName("twitter_html")
    val twitterHtml: String,

    @SerializedName("information")
    val information: ContentInformation
): Content(), Serializable