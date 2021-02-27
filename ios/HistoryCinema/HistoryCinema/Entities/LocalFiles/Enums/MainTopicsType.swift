//
//  MainTopicsType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

enum MainTopicsType: String {
    case awards, directors
    case historyCinema = "history_cinema"
    case milMovies = "mil_movies"
    
    static func getMainTopicsType(by name: String) -> MainTopicsType {
        switch name {
        case "awards":
            return .awards
        case "directors":
            return .directors
        case "history_cinema":
            return .historyCinema
        case "mil_movies":
            return .milMovies
        default:
            return .historyCinema
        }
    }
}
