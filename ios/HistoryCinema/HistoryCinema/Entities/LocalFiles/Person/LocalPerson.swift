//
//  LocalPerson.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class LocalPerson: BaseLocalModel {
    var id : Int?
    var birthday : Date?
    var knownForDepartment : String?
    var placeOfBirth : String?
    var homepage : String?
    var profilePath : String?
    var imdbId : String?
    var deathday : String?
    var name : String?
    var alsoKnownAs : [String]?
    var biography : String?
    var adult : Bool?
    var gender : Int?
    var popularity : Double?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        id                                                      <- map["id"]
        birthday                                                <- (map["birthday"], DateFormatTransform("yyyy-MM-dd"))
        knownForDepartment                                      <- map["known_for_department"]
        placeOfBirth                                            <- map["place_of_birth"]
        homepage                                                <- map["homepage"]
        profilePath                                             <- map["profile_path"]
        imdbId                                                  <- map["imdb_id"]
        deathday                                                <- map["deathday"]
        name                                                    <- map["name"]
        alsoKnownAs                                             <- map["also_known_as"]
        biography                                               <- map["biography"]
        adult                                                   <- map["adult"]
        gender                                                  <- map["gender"]
        popularity                                              <- map["popularity"]
    }
}
