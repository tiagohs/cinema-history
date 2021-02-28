//
//  Page.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class Page: BaseLocalModel {
    var number: Int!
    var contentList: [Content]!
    
    required init?(map: Map) {
        super.init(map: map)
    }
    
    override func mapping(map: Map) {
        super.mapping(map: map)
        
        number                                      <- map["number"]
        contentList                                 <- map["content_list"]
    }
}
