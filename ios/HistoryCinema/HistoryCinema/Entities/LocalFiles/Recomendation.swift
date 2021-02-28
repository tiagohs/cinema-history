//
//  Recomendation.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class Recomendation: BaseLocalModel {
    var title: String?
    var subtitle: String?
    var description: String?
    var link: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        title                                                       <- map["title"]
        subtitle                                                    <- map["subtitle"]
        description                                                 <- map["description"]
        link                                                        <- map["link"]
    }
}
