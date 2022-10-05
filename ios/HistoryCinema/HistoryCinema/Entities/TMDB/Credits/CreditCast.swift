//
//  CreditCast.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct CreditCast: BaseModel {
    var id : Int?
    var posterPath : String?
    var adult : Bool?
    var backdropPath : String?
    var voteCount : Int?
    var video : Bool?
    var popularity : Double?
    var genreIds : [Int]?
    var originalLanguage : String?
    var title : String?
    var originalTitle : String?
    var releaseDate : String?
    var character : String?
    var overview : String?
    var creditId : String?
    
    enum CodingKeys: String, CodingKey {
        case id, overview, adult, video, title, character, popularity
        case posterPath = "poster_path"
        case backdropPath = "backdrop_path"
        case voteCount = "vote_count"
        case genreIds = "genre_ids"
        case originalLanguage = "original_language"
        case originalTitle = "original_title"
        case releaseDate = "release_date"
        case creditId = "credit_id"
    }
}
