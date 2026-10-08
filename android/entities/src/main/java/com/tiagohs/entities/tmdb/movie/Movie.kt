package com.tiagohs.entities.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.tiagohs.entities.dto.MovieFilmographyDTO
import com.tiagohs.entities.omdb.OMDBResult
import com.tiagohs.entities.tmdb.ExternalIds
import com.tiagohs.entities.tmdb.Image
import com.tiagohs.entities.tmdb.Translation
import com.tiagohs.entities.tmdb.TranslationMovieData
import com.tiagohs.entities.tmdb.TranslationsResult
import java.io.Serializable


data class Movie(

    @SerializedName("adult") val adult: Boolean? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("budget") val budget: Long? = null,
    @SerializedName("belongs_to_collection") val belongsToCollection: Collection? = null,
    @SerializedName("genres") val genres: List<Genres>? = null,
    @SerializedName("homepage") val homepage: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("imdb_id") val imdbId: String? = null,
    @SerializedName("original_language") val originalLanguage: String? = null,
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("popularity") val popularity: Double? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("production_companies") val productionCompanies: List<ProductionCompanies>? = null,
    @SerializedName("production_countries") val productionCountries: List<ProductionCountries>? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("revenue") val revenue: Long? = null,
    @SerializedName("runtime") val runtime: Int? = null,
    @SerializedName("spoken_languages") val spokenLanguages: List<SpokenLanguages>? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("tagline") val tagline: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("video") val video: Boolean? = null,
    @SerializedName("vote_average") val voteAverage: Double? = null,
    @SerializedName("vote_count") val voteCount: Int? = null,
    @SerializedName("videos") val videos: MovieVideos? = null,
    @SerializedName("images") var images: MovieImages? = null,
    @SerializedName("keywords") val keywords: KeywordList? = null,
    @SerializedName("releases") val releases: Releases? = null,
    @SerializedName("similar_movies") val similarMovies: SimilarMovies? = null,
    @SerializedName("credits") val credits: Credits? = null,
    @SerializedName("genre_ids") val genreIds: List<Int>? = null,
    @SerializedName("translations") val translations: TranslationsResult<TranslationMovieData>? = null,
    @SerializedName("external_ids") val externalIds: ExternalIds? = null
) : Serializable {

    val trailerUrlKey: String?
        get() = videos?.videoList?.find { it.type == "Trailer" }?.key
            ?: videos?.videoList?.firstOrNull()?.key

    var omdbResult: OMDBResult? = null
    var extraInfo: MovieExtraInfo? = null
    var directorMovies: List<MovieFilmographyDTO>? = null
    var movieCollection: Collection? = null

    var allImages: List<Image>? = null

    fun setupImages() {
        val allImages = ArrayList<Image>(images?.backdrops ?: emptyList())
        allImages.addAll(images?.posters ?: emptyList())

        this.allImages = allImages
    }

    /** Título no idioma do app. [appLanguage] é uma tag como "pt-BR", "en-US" ou "es-MX". */
    fun getMovieTitleFromAppLanguage(appLanguage: String): String {
        val translated = findTranslation(appLanguage) { it.data?.title }
        if (!translated.isNullOrBlank()) return translated

        val originalTitle = translations?.translations?.find { it.iso_639_1 == originalLanguage }?.data?.title
        if (!originalTitle.isNullOrBlank()) {
            return originalTitle
        }

        return title ?: originalTitle ?: ""
    }

    /** Sinopse no idioma do app. [appLanguage] é uma tag como "pt-BR", "en-US" ou "es-MX". */
    fun getMovieSummaryFromAppLanguage(defaultSummary: String, appLanguage: String): String {
        val translated = findTranslation(appLanguage) { it.data?.overview }
        if (!translated.isNullOrBlank()) return translated

        val originalOverview =
            translations?.translations?.find { it.iso_639_1 == originalLanguage }?.data?.overview
        if (!originalOverview.isNullOrBlank()) {
            return originalOverview
        }

        return originalLanguage ?: defaultSummary
    }

    /** Procura a tradução exata (idioma + país) e, se não houver, qualquer país do mesmo idioma. */
    private fun findTranslation(languageTag: String, field: (Translation<TranslationMovieData>) -> String?): String? {
        val all = translations?.translations ?: return null
        val language = languageTag.substringBefore('-')
        val country = languageTag.substringAfter('-', "")

        val exact = all.find { it.iso_639_1 == language && it.iso_3166_1 == country }?.let(field)
        if (!exact.isNullOrBlank()) return exact

        return all.filter { it.iso_639_1 == language }.firstNotNullOfOrNull { t -> field(t)?.takeIf { it.isNotBlank() } }
    }
}
