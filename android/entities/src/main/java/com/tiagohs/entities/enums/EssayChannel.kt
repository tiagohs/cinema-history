package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class EssayChannel(
    var channelType: String,
    var imagePath: String,
    var url: String,
    var channelName: String
): Serializable {
    @SerializedName("lessons_from_the_screenplay")
    LESSONS_FROM_SCREENPLAY(
        channelType = "lessons_from_the_screenplay",
        imagePath = "https://8hours-prod.s3.amazonaws.com/uploads/2019/04/unnamed-2-4.jpg",
        url = "https://www.youtube.com/c/LessonsfromtheScreenplay",
        channelName = "Lessons from the Screenplay"
    ),
    @SerializedName("allerix_films")
    ALLERIX_FILMS(
        channelType = "allerix_films",
        imagePath = "https://8hours-prod.s3.amazonaws.com/uploads/2019/01/user-3301-thumbnail.jpg",
        url = "https://www.youtube.com/channel/UCe8JT_-iNjMC2ZhiEQB57Gg",
        channelName = "Allerix Films"
    ),
    @SerializedName("alpha_alpaca_pack")
    ALPHA_ALPACA_PACK(
        channelType = "alpha_alpaca_pack",
        imagePath = "https://8hours-prod.s3.amazonaws.com/uploads/2019/02/Alpha-Alpaca-Pack.jpg",
        url = "https://www.youtube.com/channel/UC3dtRhnPOJz4JrTZA7kj85g",
        channelName = "Alpha-Alpaca-Pack"
    ),
    @SerializedName("now_you_see_it")
    NOW_YOU_SEE_IT(
        channelType = "now_you_see_it",
        imagePath = "https://8hours-prod.s3.amazonaws.com/uploads/2019/01/user-3411-thumbnail.jpg",
        url = "https://www.youtube.com/channel/UCWTFGPpNQ0Ms6afXhaWDiRw/about",
        channelName = "Now You See It"
    ),
    @SerializedName("the_midas_touch")
    THE_MIDAS_TOUCH(
        channelType = "the_midas_touch",
        imagePath = "https://yt3.ggpht.com/ytc/AAUvwnj_Gob89-ZMOw5MOaIMS023lD2Vz_nUmbmbiBjiMw=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCdHXNk-O8aXWBKmm9Mr3law",
        channelName = "The Midas Touch"
    ),
    @SerializedName("the_new_york_times")
    THE_NEW_YORK_TIMES(
        channelType = "the_new_york_times",
        imagePath = "https://8hours-prod.s3.amazonaws.com/uploads/2019/05/unnamed-5.jpg",
        url = "https://www.youtube.com/channel/UCqnbDFdCpuN8CMEg0VuEBqA",
        channelName = "The New York Times"
    ),
    @SerializedName("wisecrack")
    WISECRACK(
        channelType = "wisecrack",
        imagePath = "https://8hours-prod.s3.amazonaws.com/uploads/2019/02/Wisecrack.jpg",
        url = "https://www.youtube.com/channel/UC6-ymYjG0SU0jUWnWh9ZzEQ",
        channelName = "Wisecrack"
    ),
    NONE("", "", "", "");

    companion object {
        fun getContentType(type: String?): EssayChannel {
            var typeEnum = NONE

            if (type == null) return typeEnum

            for (typeValue in values()) {
                if (typeValue.channelType == type) {
                    typeEnum = typeValue
                    break
                }
            }

            return typeEnum
        }
    }
}