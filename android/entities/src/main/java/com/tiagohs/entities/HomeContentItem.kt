package com.tiagohs.entities

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.enums.MainTopicsType
import com.tiagohs.entities.image.Image

class HomeContentItem(

    @SerializedName("main_topic_type")
    var mainTopicType: MainTopicsType,

    @SerializedName("image")
    var image: Image,

    @SerializedName("dark_mode")
    var darkMode: Boolean
)