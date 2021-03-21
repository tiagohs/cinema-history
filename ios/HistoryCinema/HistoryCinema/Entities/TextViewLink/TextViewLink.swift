//
//  HtmlTextModel.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import Foundation
import ObjectMapper

class TextViewLink: BaseLocalModel {
    var type: TextViewUrlType!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        type <- (map["type"], EnumTransform<TextViewUrlType>())
    }
    
    static func getTextViewLink(from dictionary: Dictionary<String, Any>, by textViewUrlType: TextViewUrlType) throws -> TextViewLink? {
        switch textViewUrlType {
        case .screen:
            return TextViewLinkScreen(JSON: dictionary)
        case .online:
            return TextViewLinkOnline(JSON: dictionary)
        default:
            return TextViewLinkOnline(JSON: dictionary)
        }
    }
}


