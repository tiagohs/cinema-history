//
//  PersonVideo.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class PersonVideo: BaseLocalModel {
    var name : String!
    var source : String!
    var type : String!
    var key : String!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        name                                               <- map["name"]
        source                                             <- map["source"]
        type                                               <- map["type"]
        key                                                <- map["key"]
    }
}
