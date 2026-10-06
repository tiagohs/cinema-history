package com.tiagohs.cinema_history.presentation.adapters.movie_details

import android.view.LayoutInflater
import com.tiagohs.cinema_history.databinding.ViewCompanyItemBinding
import com.tiagohs.cinema_history.databinding.AdapterMovieInfoProductionBinding
import androidx.constraintlayout.widget.Constraints
import com.tiagohs.cinema_history.R
import com.tiagohs.cinema_history.presentation.adapters.config.BaseViewHolder
import com.tiagohs.entities.enums.ImageSize
import com.tiagohs.entities.movie_info.MovieInfo
import com.tiagohs.entities.tmdb.movie.ProductionCompanies
import com.tiagohs.helpers.extensions.*
import java.util.*


class MovieInfoProductionViewHolder(
    private val binding: AdapterMovieInfoProductionBinding
) : BaseViewHolder<MovieInfo>(binding) {

    override fun bind(item: MovieInfo, position: Int) {
        super.bind(item, position)
        val movie = item.movie
        val companies = movie.productionCompanies ?: return

        companies.forEach { bindCompany(it) }
    }

    private fun bindCompany(company: ProductionCompanies) {
        val context = itemView.context
        val itemBinding = ViewCompanyItemBinding.inflate(LayoutInflater.from(context), null, false)
        val layoutParams = Constraints.LayoutParams(
            Constraints.LayoutParams.MATCH_PARENT,
            Constraints.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 10.convertIntToDp(context))
        }
        itemBinding.companyName.setResourceText(company.name)
        itemBinding.root.layoutParams = layoutParams

        company.originCountry?.let {
            val countryName = Locale("", it).displayCountry

            if (!countryName.isNullOrEmpty()) {
                itemBinding.companyCountry.show()
                itemBinding.companyCountry.text = context.getString(R.string.country_format, countryName)
            }

        }

        company.logoPath?.imageUrlFromTMDB(ImageSize.LOGO_300)
            ?.let { itemBinding.companyImage.loadImage(it, itemView.context.getString(R.string.movie_company_image_description, company.name),null, scaleType = "center_inside") }

        binding.companiesProductionContainer.addView(itemBinding.root)
    }
}