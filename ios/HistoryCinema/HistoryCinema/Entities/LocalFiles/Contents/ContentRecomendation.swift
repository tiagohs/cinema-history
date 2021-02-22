//
//  ContentRecomendation.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentRecomendation: Content {
    var list: [Recomendation]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        list         <- map["list"]
    }
}
