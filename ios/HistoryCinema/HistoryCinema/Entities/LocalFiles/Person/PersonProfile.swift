//
//  PersonProfile.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class PersonProfile: BaseLocalModel {
    var years : String!
    var content : String!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        years                                               <- map["years"]
        content                                             <- map["content"]
    }
}
