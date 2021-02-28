//
//  PersonExtraInfo.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class PersonExtraInfo: BaseLocalModel {
    var id : Int!
    var name : String?
    var customName : String?
    var highlightImagePath : String?
    var quote : String?
    var profile : [PersonProfile]?
    var awards : String?
    var videos : [PersonVideo]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        id                                                  <- map["id"]
        name                                                <- map["name"]
        customName                                          <- map["custom_name"]
        highlightImagePath                                  <- map["highlight_image"]
        quote                                               <- map["quote"]
        profile                                             <- map["profile"]
        awards                                              <- map["awards"]
        videos                                              <- map["videos"]
    }
}
