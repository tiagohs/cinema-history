//
//  ReferenceType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation

enum ReferenceType: String {
    case media
    case text
    
    static func getReferenceType(by name: String) -> ReferenceType {
        switch name {
        case "text":
            return .text
        case "media":
            return .media
        default:
            return .text
        }
    }
}
