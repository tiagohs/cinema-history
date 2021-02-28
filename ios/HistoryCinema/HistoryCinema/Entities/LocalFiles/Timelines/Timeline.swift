//
//  Timeline.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class Timeline: BaseLocalModel {
    var type: TimelineType!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        type                               <- (map["type"], EnumTransform<TimelineType>())
    }
    
    static func getTimeline(from dictionary: Dictionary<String, Any>, by timelineType: TimelineType) throws -> Timeline? {
        switch timelineType {
        case .item:
            return TimelineItem(JSON: dictionary)
        case .footer:
            return TimelineFooter(JSON: dictionary)
        case .title:
            return TimelineTitle(JSON: dictionary)
        }
    }
}
