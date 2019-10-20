package com.tiagohs.cinema_history.models.tmdb.movie

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ProductionCompanies (
	@SerializedName("id") val id : Int? = null,
	@SerializedName("logo_path") val logoPath : String? = null,
	@SerializedName("name") val name : String? = null,
	@SerializedName("origin_country") val originCountry : String? = null
): Serializable