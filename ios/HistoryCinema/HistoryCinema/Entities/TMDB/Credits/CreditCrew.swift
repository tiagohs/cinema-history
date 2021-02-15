//
//  CreditCrew.swift
//  popmovies
//
//  Created by Tiago Silva on 20/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct CreditCrew: BaseModel {
    var id : Int?
    var department : String?
    var originalLanguage : String?
    var originalTitle : String?
    var job : String?
    var overview : String?
    var genreIds : [Int]?
    var video : Bool?
    var creditId : String?
    var releaseDate : Date?
    var popularity : Double?
    var voteAverage : Double?
    var voteCount : Int?
    var title : String?
    var adult : Bool?
    var backdropPath : String?
    var posterPath : String?
    
    enum CodingKeys: String, CodingKey {
        case id, department, job, overview, video, popularity, title, adult
        case originalLanguage = "original_language"
        case originalTitle = "original_title"
        case genreIds = "genre_ids"
        case creditId = "credit_id"
        case releaseDate = "release_date"
        case voteAverage = "vote_average"
        case voteCount = "vote_count"
        case backdropPath = "backdrop_path"
        case posterPath = "poster_path"
    }
}
