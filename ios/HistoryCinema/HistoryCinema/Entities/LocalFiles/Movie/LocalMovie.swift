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
    
    static let exampleMovie = LocalMovie(JSONString: "{\n          \"vote_count\": 978,\n          \"popularity\": 15.385,\n          \"id\": 775,\n          \"video\": false,\n          \"media_type\": \"movie\",\n          \"vote_average\": 8,\n          \"title\": \"A Trip to the Moon\",\n          \"release_date\": \"1902-04-17\",\n          \"original_language\": \"fr\",\n          \"original_title\": \"Le Voyage dans la Lune\",\n          \"genre_ids\": [\n            12,\n            14,\n            878\n          ],\n          \"backdrop_path\": \"\\/jeC1kzwWnZTuXl7xF4E5D70BD8c.jpg\",\n          \"adult\": false,\n          \"overview\": \"Professor Barbenfouillis and five of his colleagues from the Academy of Astronomy travel to the Moon aboard a rocket propelled by a giant cannon. Once on the lunar surface, the bold explorers face the many perils hidden in the caves of the mysterious satellite.\",\n          \"poster_path\": \"\\/aaNIFWrq6eGi259APbB5yaqBFdm.jpg\"\n        }")!
}
