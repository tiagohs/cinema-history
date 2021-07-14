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
        url = "https://www.youtube.com/channel/UCWTFGPpNQ0Ms6afXhaWDiRw",
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
    @SerializedName("studio_binder")
    STUDIO_BINDER(
        channelType = "studio_binder",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLRj_XKhs2M4hYC0hrfqDP3pU31M8c3a_aBsDgs3fA=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCUFoQUaVRt3MVFxqwPUMLCQ",
        channelName = "StudioBinder"
    ),
    @SerializedName("fames_focus")
    FAMES_FOCUS(
        channelType = "fames_focus",
        imagePath = "https://8hours-prod.s3.amazonaws.com/uploads/2020/02/Fame-Focus_Avatar.jpg",
        url = "https://www.youtube.com/channel/UCfzuJecdM0uhX7HfICewt3Q",
        channelName = "Fames Focus"
    ),
    @SerializedName("cosmavoid")
    COSMAVOID(
        channelType = "cosmavoid",
        imagePath = "https://8hours-prod.s3.amazonaws.com/uploads/2019/06/Cosmavoid.jpg",
        url = "https://www.youtube.com/channel/UCcjI2G98kWgw7a-JjWt6kZw",
        channelName = "Cosmavoid"
    ),
    @SerializedName("insider")
    INSIDER(
        channelType = "insider",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLQx1K-Ns1FeQwz4s25pqaYzCUJmOOuQ_LiOSeqNag=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCHJuQZuzapBh-CuhRYxIZrg",
        channelName = "Insider"
    ),
    @SerializedName("fandor")
    FANDOR(
        channelType = "fandor",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLRozsB_pn8uCJ8a9EtJJ4WPqrgSeHLCi_SLIndG=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCkeBOIrsgk0EyJwg-hHs7MA",
        channelName = "Insider"
    ),
    @SerializedName("just_write")
    JUST_WRITE(
        channelType = "just_write",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLSY6-239g14R2bGFOKPT1_j-vXtxv25QOM2cc6rsg=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/user/mythicalsage",
        channelName = "Just Write"
    ),
    @SerializedName("kaptainkristian")
    KAPITAIN_KRISTIAN(
        channelType = "kaptainkristian",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLQYu1Yql-icWldIbCy5nVZrIMRCb5PypsaCmH_XOA=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCuPgdqQKpq4T4zeqmTelnFg",
        channelName = "kaptainkristian"
    ),
    @SerializedName("channel_awesome")
    CHANNEL_AWESOME(
        channelType = "channel_awesome",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLRWM8dxXjyH0VQGSw4DE-y4NKSUriGEAhQOey4xMg=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCiH828EtgQjTyNIMH6YiOSw",
        channelName = "Channel Awesome"
    ),
    @SerializedName("alex_day")
    ALEX_DAY(
        channelType = "alex_day",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLQeKRpvQAaNAj0vMJwzf68iAQ8TLj4jdE9EdccZ0A=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCM6hs_4aB32MgKQA96cdo-g",
        channelName = "Alex Day"
    ),
    @SerializedName("rossatron")
    ROSSATRON(
        channelType = "rossatron",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLRyf3i1VIAJ9SUZy3gL4iR_sjnLchi2dQFTmatn=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCxUR9wLuzgsvA6mpgKtiqGw",
        channelName = "Rossatron"
    ),
    @SerializedName("like_stories_of_old")
    LIKE_STORIES_OF_OLD(
        channelType = "like_stories_of_old",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLRbUKSefnaSbd-gk3LwKysyy2wqOYagw7YFZIL2WQ=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UCs7nPQIEba0T3tGOWWsZpJQ",
        channelName = "Like Stories of Old"
    ),
    @SerializedName("storyStreet")
    STORYSTREET(
        channelType = "storyStreet",
        imagePath = "https://yt3.ggpht.com/ytc/AKedOLRhQ0-uRTVg2jbVFwo_dnGOeXx8rzqypmNxjVwLww=s176-c-k-c0x00ffffff-no-rj",
        url = "https://www.youtube.com/channel/UC_1tSdT5U1Y2AL6kBFXw8Vw",
        channelName = "StoryStreet"
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