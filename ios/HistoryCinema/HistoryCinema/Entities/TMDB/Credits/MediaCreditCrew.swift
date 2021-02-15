//
//  CreditCrew.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import ObjectMapper

struct MediaCreditCrew: BaseModel {
    var id : Int?
    var name : String?
    var profilePath : String?
    var department : String?
    var gender : Int?
    var creditId : String?
    var job : String?
    
    enum CodingKeys: String, CodingKey {
        case creditId, department, gender, id, job, name
        case profilePath = "profile_path"
    }
}
