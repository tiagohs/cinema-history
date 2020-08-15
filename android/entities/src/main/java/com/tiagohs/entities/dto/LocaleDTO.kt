package com.tiagohs.entities.dto

import java.util.*

data class LocaleDTO(
    var displayCountryName: String? = null,
    var displayCountryLanguage: String? = null,
    var isoCountry: String? = null,
    var isoLanguage: String? = null,
    var locale: Locale? = null
)