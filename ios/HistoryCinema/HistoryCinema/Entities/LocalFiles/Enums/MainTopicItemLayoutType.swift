//
//  MainTopicItemLayoutType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation

enum MainTopicItemLayoutType: String {
    case card
    case card_full
    case full
    case quote;
    
    static func getMainTopicItemLayoutType(by name: String) -> MainTopicItemLayoutType {
        switch name {
        case "card":
            return .card
        case "card_full":
            return .card_full
        case "full":
            return .full
        case "quote":
            return .quote
        default:
            return .card
        }
    }
}
