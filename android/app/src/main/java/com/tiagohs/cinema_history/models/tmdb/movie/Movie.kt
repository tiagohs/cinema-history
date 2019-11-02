package com.tiagohs.cinema_history.models.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.tiagohs.cinema_history.models.tmdb.ExternalIds
import com.tiagohs.cinema_history.models.tmdb.TranslationMovieData
import com.tiagohs.cinema_history.models.tmdb.TranslationsResult
import java.io.Serializable


data class Movie (

    @SerializedName("adult") val adult : Boolean? = null,
    @SerializedName("backdrop_path") val backdropPath : String? = null,
    @SerializedName("budget") val budget : Long? = null,
    @SerializedName("genres") val genres : List<Genres>? = null,
    @SerializedName("homepage") val homepage : String? = null,
    @SerializedName("id") val id : Int? = null,
    @SerializedName("imdb_id") val imdbId : String? = null,
    @SerializedName("original_language") val originalLanguage : String? = null,
    @SerializedName("original_title") val originalTitle : String? = null,
    @SerializedName("overview") val overview : String? = null,
    @SerializedName("popularity") val popularity : Double? = null,
    @SerializedName("poster_path") val posterPath : String? = null,
    @SerializedName("production_companies") val productionCompanies : List<ProductionCompanies>? = null,
    @SerializedName("production_countries") val productionCountries : List<ProductionCountries>? = null,
    @SerializedName("release_date") val releaseDate : String? = null,
    @SerializedName("revenue") val revenue : Long? = null,
    @SerializedName("runtime") val runtime : Int? = null,
    @SerializedName("spoken_languages") val spokenLanguages : List<SpokenLanguages>? = null,
    @SerializedName("status") val status : String? = null,
    @SerializedName("tagline") val tagline : String? = null,
    @SerializedName("title") val title : String? = null,
    @SerializedName("video") val video : Boolean? = null,
    @SerializedName("vote_average") val voteAverage : Double? = null,
    @SerializedName("vote_count") val voteCount : Int? = null,
    @SerializedName("videos") val videos : MovieVideos? = null,
    @SerializedName("images") val images : MovieImages? = null,
    @SerializedName("keywords") val keywords : KeywordList? = null,
    @SerializedName("releases") val releases : Releases? = null,
    @SerializedName("similar_movies") val similarMovies : SimilarMovies? = null,
    @SerializedName("credits") val credits : Credits? = null,
    @SerializedName("genre_ids") val genreIds : List<Int>? = null,
    @SerializedName("translations") val translations : TranslationsResult<TranslationMovieData>? = null,
    @SerializedName("external_ids") val externalIds : ExternalIds? = null


): Serializable