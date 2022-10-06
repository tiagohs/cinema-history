//
//  Translation.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct Translation: BaseModel {
    var id: Int? = UUID().hashValue
    var iso_3166_1 : String?
    var iso_639_1 : String?
    var name : String?
    var englishName : String?
    var data : TranslationData?
    
    enum CodingKeys: String, CodingKey {
        case name, data
        case iso_3166_1 = "iso_3166_1"
        case iso_639_1 = "iso_639_1"
        case englishName = "english_name"
    }
}

struct TranslationPerson: BaseModel {
    var id: Int? = UUID().hashValue
    var iso_3166_1 : String?
    var iso_639_1 : String?
    var name : String?
    var englishName : String?
    var data : TranslationPersonData?
    
    enum CodingKeys: String, CodingKey {
        case name, data
        case iso_3166_1 = "iso_3166_1"
        case iso_639_1 = "iso_639_1"
        case englishName = "english_name"
    }
}
