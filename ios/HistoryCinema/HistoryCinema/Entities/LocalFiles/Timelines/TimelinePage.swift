//
//  TimelinePage.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class TimelinePage: BaseLocalModel {
    var id: Int!
    var color: String!
    var titleTextColor: String!
    var timelineList: [Timeline]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        id                                      <- map["id"]
        color                                   <- map["color"]
        titleTextColor                          <- map["title_text_color"]
        timelineList                            <- map["timeline_list"]
    }
}
