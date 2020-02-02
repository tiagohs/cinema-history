package com.tiagohs.cinema_history.ui.adapters.movie_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.RecyclerView
import com.tiagohs.cinema_history.R
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.helpers.extensions.loadImage
import com.tiagohs.helpers.extensions.*
import com.tiagohs.entities.tmdb.movie.Movie
import com.tiagohs.entities.tmdb.movie.ProductionCompanies
import kotlinx.android.synthetic.main.adapter_movie_info_production.view.*
import kotlinx.android.synthetic.main.view_company_item.view.*
import java.util.*


class MovieInfoProductionViewHolder(
    val context: Context?,
    view: View
): RecyclerView.ViewHolder(view) {

    fun bindMovieInfo(movie: Movie) {
        val companies = movie.productionCompanies ?: return

        companies.forEach { bindCompany(it) }
    }

    private fun bindCompany(company: ProductionCompanies) {
        val view = LayoutInflater.from(context).inflate(R.layout.view_company_item, null, false)
        val layoutParams = Constraints.LayoutParams(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.WRAP_CONTENT)

        layoutParams.setMargins(0, 0, 0, 10.convertIntToDp(context))

        view.companyName.text = company.name
        view.layoutParams = layoutParams

        company.originCountry?.let {
            val countryName = Locale("", it).displayCountry

            if (!countryName.isNullOrEmpty()) {
                view.companyCountry.visibility = View.VISIBLE
                view.companyCountry.text = "País: $countryName"
            }

        }

        company.logoPath?.imageUrlFromTMDB(ImageSize.LOGO_300)?.let { view.companyImage.loadImage(it, null, scaleType = "center_inside") }

        itemView.companiesProductionContainer.addView(view)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_production
    }
}