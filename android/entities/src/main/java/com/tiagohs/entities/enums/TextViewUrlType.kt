package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName

enum class TextViewUrlType(
    val type: String
){
    @SerializedName("online")
    ONLINE("online"),

    @SerializedName("screen")
    SCREEN("screen");

    companion object {

        fun getContentType(type: String): TextViewUrlType {
            var typeEnum = ONLINE

            for (typeValue in values()) {
                if (typeValue.type == type) {
                    typeEnum = typeValue
                    break
                }
            }

            return typeEnum
        }
    }
}