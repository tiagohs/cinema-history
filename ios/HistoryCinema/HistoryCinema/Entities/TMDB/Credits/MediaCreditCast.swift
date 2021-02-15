//
//  MediaCreditCast.swift
//  popmovies
//
//  Created by Tiago Silva on 20/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//


struct MediaCreditCast: BaseModel {
    var id : Int?
    var name : String?
    var profilePath : String?
    var castId : Int?
    var character : String?
    var creditId : String?
    var gender : Int?
    var order : Int?
    
    enum CodingKeys: String, CodingKey {
        case id, character, order, name, gender
        case castId = "cast_id"
        case creditId = "credit_id"
        case profilePath = "profile_path"
    }
}
