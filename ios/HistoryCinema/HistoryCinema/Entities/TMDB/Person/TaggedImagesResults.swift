//
//  TaggedImagesResults.swift
//  popmovies
//
//  Created by Tiago Silva on 20/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct TaggedImagesResults: BaseModel {
    var id: Int? = UUID().hashValue
    var iso639_1 : String?
    var voteCount : Int?
    var mediaType : String?
    var filePath : String?
    var aspectRatio : Double?
    var media : Movie?
    var height : Int?
    var voteAverage : Double?
    var width : Int?
    
    enum CodingKeys: String, CodingKey {
        case media, height, width
        case iso639_1 = "iso_639_1"
        case voteCount = "vote_count"
        case mediaType = "media_type"
        case filePath = "file_path"
        case aspectRatio = "aspect_ratio"
        case voteAverage = "vote_average"
    }
}
