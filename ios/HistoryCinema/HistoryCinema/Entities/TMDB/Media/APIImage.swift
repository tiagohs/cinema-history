//
//  Artwork.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct APIImage: BaseModel {
    var id: Int? = UUID().hashValue
    var aspectRatio : Double?
    var filePath : String?
    var height : Int?
    var language : Double?
    var voteAverage : Int?
    var voteCount : Int?
    var width : Int?
    
    enum CodingKeys: String, CodingKey {
        case height, width
        case aspectRatio = "aspect_ratio"
        case filePath = "file_path"
        case language = "iso_639_1"
        case voteAverage = "vote_average"
        case voteCount = "vote_count"
    }
}

struct Images: BaseModel {
    var id: Int? = UUID().hashValue
    var backdrops : [APIImage]?
    var posters : [APIImage]?
    var profiles : [APIImage]?
}

struct ImageResultDTO {
    var id: Int? = UUID().hashValue
    var images: [APIImage] = []
    var translations: [Translation] = []
}
