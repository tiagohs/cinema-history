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
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/lessons_from_the_screenplay.webp",
        url = "https://www.youtube.com/c/LessonsfromtheScreenplay",
        channelName = "Lessons from the Screenplay"
    ),
    @SerializedName("allerix_films")
    ALLERIX_FILMS(
        channelType = "allerix_films",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/allerix_films.webp",
        url = "https://www.youtube.com/channel/UCe8JT_-iNjMC2ZhiEQB57Gg",
        channelName = "Allerix Films"
    ),
    @SerializedName("alpha_alpaca_pack")
    ALPHA_ALPACA_PACK(
        channelType = "alpha_alpaca_pack",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/alpha_alpaca_pack.webp",
        url = "https://www.youtube.com/channel/UC3dtRhnPOJz4JrTZA7kj85g",
        channelName = "Alpha-Alpaca-Pack"
    ),
    @SerializedName("now_you_see_it")
    NOW_YOU_SEE_IT(
        channelType = "now_you_see_it",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/now_you_see_it.webp",
        url = "https://www.youtube.com/channel/UCWTFGPpNQ0Ms6afXhaWDiRw",
        channelName = "Now You See It"
    ),
    @SerializedName("the_midas_touch")
    THE_MIDAS_TOUCH(
        channelType = "the_midas_touch",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/the_midas_touch.webp",
        url = "https://www.youtube.com/channel/UCdHXNk-O8aXWBKmm9Mr3law",
        channelName = "The Midas Touch"
    ),
    @SerializedName("the_new_york_times")
    THE_NEW_YORK_TIMES(
        channelType = "the_new_york_times",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/the_new_york_times.webp",
        url = "https://www.youtube.com/channel/UCqnbDFdCpuN8CMEg0VuEBqA",
        channelName = "The New York Times"
    ),
    @SerializedName("wisecrack")
    WISECRACK(
        channelType = "wisecrack",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/wisecrack.webp",
        url = "https://www.youtube.com/channel/UC6-ymYjG0SU0jUWnWh9ZzEQ",
        channelName = "Wisecrack"
    ),
    @SerializedName("studio_binder")
    STUDIO_BINDER(
        channelType = "studio_binder",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/studio_binder.webp",
        url = "https://www.youtube.com/channel/UCUFoQUaVRt3MVFxqwPUMLCQ",
        channelName = "StudioBinder"
    ),
    @SerializedName("fames_focus")
    FAMES_FOCUS(
        channelType = "fames_focus",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/fames_focus.webp",
        url = "https://www.youtube.com/channel/UCfzuJecdM0uhX7HfICewt3Q",
        channelName = "Fames Focus"
    ),
    @SerializedName("cosmavoid")
    COSMAVOID(
        channelType = "cosmavoid",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/cosmavoid.webp",
        url = "https://www.youtube.com/channel/UCcjI2G98kWgw7a-JjWt6kZw",
        channelName = "Cosmavoid"
    ),
    @SerializedName("insider")
    INSIDER(
        channelType = "insider",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/insider.webp",
        url = "https://www.youtube.com/channel/UCHJuQZuzapBh-CuhRYxIZrg",
        channelName = "Insider"
    ),
    @SerializedName("fandor")
    FANDOR(
        channelType = "fandor",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/fandor.webp",
        url = "https://www.youtube.com/channel/UCkeBOIrsgk0EyJwg-hHs7MA",
        channelName = "Insider"
    ),
    @SerializedName("just_write")
    JUST_WRITE(
        channelType = "just_write",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/just_write.webp",
        url = "https://www.youtube.com/user/mythicalsage",
        channelName = "Just Write"
    ),
    @SerializedName("kaptainkristian")
    KAPITAIN_KRISTIAN(
        channelType = "kaptainkristian",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/kaptainkristian.webp",
        url = "https://www.youtube.com/channel/UCuPgdqQKpq4T4zeqmTelnFg",
        channelName = "kaptainkristian"
    ),
    @SerializedName("channel_awesome")
    CHANNEL_AWESOME(
        channelType = "channel_awesome",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/channel_awesome.webp",
        url = "https://www.youtube.com/channel/UCiH828EtgQjTyNIMH6YiOSw",
        channelName = "Channel Awesome"
    ),
    @SerializedName("alex_day")
    ALEX_DAY(
        channelType = "alex_day",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/alex_day.webp",
        url = "https://www.youtube.com/channel/UCM6hs_4aB32MgKQA96cdo-g",
        channelName = "Alex Day"
    ),
    @SerializedName("rossatron")
    ROSSATRON(
        channelType = "rossatron",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/rossatron.webp",
        url = "https://www.youtube.com/channel/UCxUR9wLuzgsvA6mpgKtiqGw",
        channelName = "Rossatron"
    ),
    @SerializedName("like_stories_of_old")
    LIKE_STORIES_OF_OLD(
        channelType = "like_stories_of_old",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/like_stories_of_old.webp",
        url = "https://www.youtube.com/channel/UCs7nPQIEba0T3tGOWWsZpJQ",
        channelName = "Like Stories of Old"
    ),
    @SerializedName("storyStreet")
    STORYSTREET(
        channelType = "storyStreet",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/storyStreet.webp",
        url = "https://www.youtube.com/channel/UC_1tSdT5U1Y2AL6kBFXw8Vw",
        channelName = "StoryStreet"
    ),
    @SerializedName("variety")
    VARIETY(
        channelType = "variety",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/variety.webp",
        url = "https://www.youtube.com/channel/UCgRQHK8Ttr1j9xCEpCAlgbQ",
        channelName = "Variety"
    ),
    @SerializedName("vox")
    VOX(
        channelType = "vox",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/vox.webp",
        url = "https://www.youtube.com/channel/UCLXo7UDZvByw2ixzpQCufnA",
        channelName = "Vox"
    ),
    @SerializedName("the_take")
    THE_TAKE(
        channelType = "the_take",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/the_take.webp",
        url = "https://www.youtube.com/channel/UCVjsbqKtxkLt7bal4NWRjJQ",
        channelName = "The Take"
    ),
    @SerializedName("wow_such_gaming")
    WOW_SUCH_GAMING(
        channelType = "wow_such_gaming",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/wow_such_gaming.webp",
        url = "https://www.youtube.com/channel/UCugqjlk-tkE_uZnG0tDbQyg",
        channelName = "Wow Such Gaming"
    ),
    @SerializedName("the_discarded_image")
    THE_DISCRDED_IMAGE(
        channelType = "the_discarded_image",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/the_discarded_image.webp",
        url = "https://www.youtube.com/channel/UCJKLIcSts_4XpVbORwzLZVw",
        channelName = "The Discarded Image"
    ),
    @SerializedName("netflix_film_club")
    NETFLIX_FILM_CLUB(
        channelType = "netflix_film_club",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/netflix_film_club.webp",
        url = "https://www.youtube.com/channel/UC_UJdqZuFhRXfeYXYrhwgJA",
        channelName = "Netflix Film Club"
    ),
    @SerializedName("every_frame_is_a_painting")
    EVERY_FRAME_IS_A_PAINTING(
        channelType = "every_frame_is_a_painting",
        imagePath = "https://website-cb5.pages.dev/cinema-history/media/channels/every_frame_is_a_painting.webp",
        url = "https://www.youtube.com/channel/UCjFqcJQXGZ6T6sxyFB-5i6A",
        channelName = "Every Frame a Painting"
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