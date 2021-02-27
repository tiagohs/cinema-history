//
//  MainTopicsType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

enum MainTopicsType: String {
    case timeline, awards, directors
    case history_cinema
    case mil_movies
    
    static func getMainTopicsType(by name: String) -> MainTopicsType {
        switch name {
        case "awards":
            return .awards
        case "timeline":
            return .timeline
        case "directors":
            return .directors
        case "history_cinema":
            return .history_cinema
        case "mil_movies":
            return .mil_movies
        default:
            return .history_cinema
        }
    }
}
