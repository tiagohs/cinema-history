package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.ProductionCompanies
import com.tiagohs.helpers.extensions.*
import kotlinx.android.synthetic.main.adapter_movie_info_production.view.*
import kotlinx.android.synthetic.main.view_company_item.view.*
import java.util.*


class MovieInfoProductionViewHolder(
    view: View
) : BaseViewHolder<MovieInfo>(view) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie
        val companies = movie.productionCompanies ?: return

        companies.forEach { bindCompany(it) }
    }

    private fun bindCompany(company: ProductionCompanies) {
        val context = containerView.context
        val view = LayoutInflater.from(context).inflate(R.layout.view_company_item, null, false)
        val layoutParams = Constraints.LayoutParams(
            Constraints.LayoutParams.MATCH_PARENT,
            Constraints.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 10.convertIntToDp(context))
        }
        view.companyName.setResourceText(company.name)
        view.layoutParams = layoutParams

        company.originCountry?.let {
            val countryName = Locale("", it).displayCountry

            if (!countryName.isNullOrEmpty()) {
                view.companyCountry.show()
                view.companyCountry.text = context.getString(R.string.country_format, countryName)
            }

        }

        company.logoPath?.imageUrlFromTMDB(ImageSize.LOGO_300)
            ?.let { view.companyImage.loadImage(it, null, scaleType = "center_inside") }

        itemView.companiesProductionContainer.addView(view)
    }

    companion object {
        const val LAYOUT_ID = R.layout.adapter_movie_info_production
    }
}