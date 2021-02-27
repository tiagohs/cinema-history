//
//  TimelineType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

enum TimelineType: String {
    case title
    case footer
    case item
    
    static func getTimelineType(by name: String) -> TimelineType {
        switch name {
        case "title":
            return .title
        case "footer":
            return .footer
        case "item":
            return .item
        default:
            return .title
        }
    }
}
