//
//  TextViewLinkOnline.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import Foundation
import ObjectMapper

class TextViewLinkOnline: TextViewLink {
    var url: String!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        url         <- map["url"]
    }
}
