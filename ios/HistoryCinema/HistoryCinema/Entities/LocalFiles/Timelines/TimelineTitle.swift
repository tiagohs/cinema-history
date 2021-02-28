//
//  TimelineTitle.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class TimelineTitle: Timeline {
    var id: Int!
    var title: String!
    var pageTitle: String!
    var next: String?
    var comingSoon: Bool?
    var previous: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        id                                      <- map["id"]
        title                                   <- map["title"]
        pageTitle                               <- map["page_title"]
        next                                    <- map["next"]
        comingSoon                              <- map["coming_soon"]
        previous                                <- map["previous"]
    }
}
