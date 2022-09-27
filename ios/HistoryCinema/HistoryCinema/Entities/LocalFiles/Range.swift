//
//  Range.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import Foundation
import ObjectMapper

class Range: BaseLocalModel {
    var from: Int!
    var to: Int!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        from                                                        <- map["from"]
        to                                                          <- map["to"]
    }
}
