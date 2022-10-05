//
//  ExternalIds.swift
//  popmovies
//
//  Created by Tiago Silva on 19/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct ExternalIds: BaseModel {
    var id: Int? = UUID().hashValue
    var freebaseId : String?
    var instagramId : String?
    var tvRageId : Int?
    var twitterId : String?
    var freebaseMid : String?
    var imdbId : String?
    var facebookId : String?
    
    enum CodingKeys: String, CodingKey {
        case freebaseId = "freebase_id"
        case instagramId = "instagram_id"
        case tvRageId = "tvrage_id"
        case twitterId = "twitter_id"
        case freebaseMid = "freebase_mid"
        case imdbId = "imdb_id"
        case facebookId = "facebook_id"
    }
}
