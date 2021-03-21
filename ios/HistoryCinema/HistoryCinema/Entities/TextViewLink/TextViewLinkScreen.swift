//
//  TextViewLinkScreen.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import Foundation
import ObjectMapper

class TextViewLinkScreen: TextViewLink {
    var screenType: TextViewLinkScreenType!
    var id: Int!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        screenType      <- (map["screen_type"], EnumTransform<TextViewLinkScreenType>())
        id              <- map["id"]
    }
}
