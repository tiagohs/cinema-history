//
//  Video.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//


struct Video: BaseModel {
    var id : String?
    var language : String?
    var country : String?
    var key : String?
    var name : String?
    var site : String?
    var size : Int?
    var type : String?
    
    enum CodingKeys: String, CodingKey {
        case id, key, name, site, size, type
        case language = "iso_639_1"
        case country = "iso_3166_1"
    }
}

class VideoResultDTO {
    var videos: [Video] = []
    var translations: [Translation] = []
}
