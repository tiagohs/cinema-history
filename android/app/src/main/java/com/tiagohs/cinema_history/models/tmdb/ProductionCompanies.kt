package com.tiagohs.cinema_history.models.tmdb

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ProductionCompanies (
	@SerializedName("id") val id : Int,
	@SerializedName("logo_path") val logoPath : String,
	@SerializedName("name") val name : String,
	@SerializedName("origin_country") val originCountry : String
): Serializable