//
//  ContentPersonList.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentPersonList: Content {
    var title: String?
    var persons: [LocalPerson]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        title                                          <- map["title"]
        persons                                      <- map["persons"]
    }
}
