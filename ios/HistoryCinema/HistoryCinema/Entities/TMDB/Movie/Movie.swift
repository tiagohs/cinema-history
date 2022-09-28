//
//  Movie.swift
//  popmovies
//
//  Created by Tiago Silva on 14/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

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
    var voteCount: Int?
    private var release_date: String?
    var releaseDate: String?
    var genreIds: [Int]?
    
    var genres: [Genre]?
    var productionCompanies: [ProductionCompany]?
    var productionCountries: [ProductionCountry]?
    var spokenLanguages: [SpokenLanguage]?
    
    // Append To Reponse
    var alternativeTitles: [AlternativeTitle]?
    var credits: MediaCreditList?
    var images: Images?
    var keywords: ResultsKeyword?
    var releases: Release?
    var videos: VideoResult?
    var translations: TranslationResults?
    var similiarMovies: ResultsMovie?
    
    var allImages: [APIImage] = []
    var isFavorite: Bool = false
    var isWatched: Bool = false
    var isWantToSee: Bool = false
    
    enum CodingKeys: String, CodingKey {
        case id, adult, budget, homepage, overview, popularity, revenue, runtime, status, title, video, credits, images, keywords, releases, videos, translations
        case releaseDate = "release_date"
        case backdropPath = "backdrop_path"
        case belongsToCollection = "belongs_to_collection"
        case genreIds = "genre_ids"
        case imdbID = "imdb_id"
        case originalLanguage = "original_language"
        case originalTitle = "original_title"
        case posterPath = "poster_path"
        case voteCount = "vote_count"
        case genres = "genres"
        
        case productionCompanies = "production_companies"
        case productionCountries = "production_countries"
        case spokenLanguages = "spoken_languages"
        
        case alternativeTitles = "alternative_titles"
        case similiarMovies = "similar_movies"
    }
    
    private func mergeImages() -> [APIImage] {
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
    
    static let exampleMovie = try! JSONDecoder().decode(Movie.self, from: "{\"adult\":false,\"backdrop_path\":\"/kJSnhBQZyCcbqxMZJuDby8ULcfS.jpg\",\"genre_ids\":[12,878],\"id\":775,\"media_type\":\"movie\",\"original_language\":\"fr\",\"original_title\":\"Le Voyage dans la Lune\",\"overview\":\"Professor Barbenfouillis and five of his colleagues from the Academy of Astronomy travel to the Moon aboard a rocket propelled by a giant cannon. Once on the lunar surface, the bold explorers face the many perils hidden in the caves of the mysterious planet.\",\"popularity\":19.8,\"poster_path\":\"/9o0v5LLFk51nyTBHZSre6OB37n2.jpg\",\"release_date\":\"1902-10-04\",\"title\":\"A Trip to the Moon\",\"video\":false,\"vote_average\":7.9,\"vote_count\":1377}".data(using: .utf8)!)
}
