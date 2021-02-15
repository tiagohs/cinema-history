//
//  Movie.swift
//  popmovies
//
//  Created by Tiago Silva on 14/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation
import ObjectMapper

struct Movie: BaseModel {
    var id: Int?
    var adult: Bool?
    var backdropPath: String?
    var belongsToCollection: String?
    var budget: Int?
    var homepage: String?
    var imdbID, originalLanguage, originalTitle, overview: String?
    var popularity: Decimal?
    var posterPath: String?
    var revenue, runtime: Int?
    var status, tagline, title: String?
    var video: Bool?
    var voteAverage: Decimal?
    var voteCount: Int?
    private var release_date: String?
    var releaseDate: Date?
    var genreIds: [Int]?
    
    var genres: [Genre]?
    var productionCompanies: [ProductionCompany]?
    var productionCountries: [ProductionCountry]?
    var spokenLanguages: [SpokenLanguage]?
    
    // Append To Reponse
    var alternativeTitles: [AlternativeTitle]?
    var credits: MediaCreditList?
    var images: Images?
    var keywords: [ResultsKeyword]?
    var releases: [Release]?
    var videos: VideoResult?
    var translations: [TranslationResults]?
    var similiarMovies: ResultsMovie?
    var reviews: [Review]?
    
    var allImages: [Image] = []
    var isFavorite: Bool = false
    var isWatched: Bool = false
    var isWantToSee: Bool = false
    
    enum CodingKeys: String, CodingKey {
        case id, adult, budget, homepage, overview, popularity, revenue, runtime, status, title, video, credits, images, keywords, releases, videos, translations, reviews, release_date
        case backdropPath = "backdrop_path"
        case belongsToCollection = "belongs_to_collection"
        case genreIds = "genre_ids"
        case imdbID = "imdb_id"
        case originalLanguage = "original_language"
        case originalTitle = "original_title"
        case posterPath = "poster_path"
        case voteAverage = "vote_average"
        case voteCount = "vote_count"
        case genres = "genres"
        
        case productionCompanies = "production_companies"
        case productionCountries = "production_countries"
        case spokenLanguages = "spoken_languages"
        
        case alternativeTitles = "alternative_titles"
        case similiarMovies = "similar_movies"
    }
    
    private func mergeImages() -> [Image] {
        let posters = self.images?.backdrops ?? []
        let backdrop = self.images?.posters ?? []
        
        if (posters.count > 3 && backdrop.count > 3) {
            return Array(backdrop[0..<3]) + Array(posters[0..<3])
        }
        
        return Array(Set(backdrop + posters))
    }
    
    static func == (lhs: Movie, rhs: Movie) -> Bool {
        return (lhs.id == rhs.id)
    }
}
