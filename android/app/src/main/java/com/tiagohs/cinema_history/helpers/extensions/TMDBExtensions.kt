package com.tiagohs.cinema_history.helpers.extensions

import com.tiagohs.cinema_history.enums.ImageSize

fun String.imageUrlFromTMDB(size: ImageSize = ImageSize.BACKDROP_780): String = "https://image.tmdb.org/t/p/${size.size}/${this}"