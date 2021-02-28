//
//  ReferenceText.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation
import ObjectMapper

class ReferenceText: Reference {
    var text: String!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        text                                                    <- map["text"]
    }
}
