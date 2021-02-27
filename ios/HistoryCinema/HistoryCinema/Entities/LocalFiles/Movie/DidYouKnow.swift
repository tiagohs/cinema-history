//
//  DidYouKnow.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class DidYouKnow: BaseLocalModel {
    var title: String?
    var description: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        title                                                   <- map["title"]
        description                                             <- map["description"]
    }
}
