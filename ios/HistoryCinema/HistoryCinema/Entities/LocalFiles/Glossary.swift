//
//  Glossary.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation
import ObjectMapper

class Glossary: BaseLocalModel {
    var name: String!
    var contentList: [Content]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        name                                                    <- map["name"]
        contentList                                             <- map["content_list"]
    }
}
