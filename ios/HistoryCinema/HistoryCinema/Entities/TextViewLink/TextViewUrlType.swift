//
//  TextViewUrlType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import Foundation


enum TextViewUrlType: String {
    case screen, online
    
    static func getTextViewUrlType(by name: String) -> TextViewUrlType {
        switch name {
        case "screen":
            return .screen
        case "online":
            return .online
        default:
            return .online
        }
    }
}
