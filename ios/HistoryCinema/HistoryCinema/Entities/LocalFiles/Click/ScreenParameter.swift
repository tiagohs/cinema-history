//
//  ScreenParameter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ScreenParameter: BaseLocalModel {
    var key: String?
    var value: String!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        key                                     <- map["key"]
        value                                   <- map["value"]
    }
}
