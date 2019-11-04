package com.tiagohs.cinema_history.models.dto

import java.io.Serializable

class MovieFilmographyDTO(
    val id: Int? = null,
    val title: String? = null,
    val posterPath: String? = null,
    val backdrop: String? = null,
    val releaseDate: String? = null,
    val overview: String? = null,
    val character: String? = null,
    val departments: String? = null,
    val year: Int? = null
): Serializable