package com.tiagohs.helpers.extensions

import com.tiagohs.entities.enums.ImageSize

fun String.imageUrlFromTMDB(size: ImageSize = ImageSize.BACKDROP_780): String = "https://image.tmdb.org/t/p/${size.size}/${this}"