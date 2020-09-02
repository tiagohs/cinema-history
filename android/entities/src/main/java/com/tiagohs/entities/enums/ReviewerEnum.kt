package com.tiagohs.entities.enums

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.R

enum class ReviewerEnum(
    val logo: Int
) {
    @SerializedName("omelete")
    OMELETE(R.drawable.img_logo_omelete),

    @SerializedName("plano_critico")
    PLANO_CRITICO(R.drawable.img_logo_plano_critico),

    @SerializedName("adoro_cinema")
    ADORO_CINEMA(R.drawable.img_logo_adoro_cinema),

    @SerializedName("unknown")
    UNKNOWN(logo = R.drawable.ic_placeholder_review)
}