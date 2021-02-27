//
//  TimelineFooter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class TimelineFooter: Timeline {
    var next: String?
    var previous: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        next                                    <- map["next"]
        previous                                <- map["previous"]
    }
}
