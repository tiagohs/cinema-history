//
//  LocalMovie.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class LocalMovie: BaseLocalModel {
    var id: Int?
    var adult: Bool?
    var backdropPath: String?
    var imdbID, originalLanguage, originalTitle, overview: String?
    var posterPath: String?
    var runtime: Int?
    var title: String?
    var releaseDate: Date?
    var genreIds: [Int]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        id                                              <- map["movies"]
        adult                                           <- map["adult"]
        backdropPath                                    <- map["backdrop_path"]
        imdbID                                          <- map["imdb_id"]
        originalLanguage                                <- map["original_language"]
        originalTitle                                   <- map["original_title"]
        overview                                        <- map["overview"]
        posterPath                                      <- map["poster_path"]
        runtime                                         <- map["runtime"]
        title                                           <- map["title"]
        releaseDate                                     <- (map["release_date"], DateFormatTransform("yyyy-MM-dd"))
        genreIds                                        <- map["genre_ids"]
    }
}
