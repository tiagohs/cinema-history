//
//  Summary.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation
import ObjectMapper

class Summary: BaseLocalModel {
    var id: Int!
    var title: String!
    var description: String!
    var image: LocalImage!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        id                                                          <- map["id"]
        title                                                       <- map["title"]
        description                                                 <- map["description"]
        image                                                       <- map["image"]
    }
}
