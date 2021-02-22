//
//  ContentText.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentText: Content {
    var contentText: String?
    var contentTitle: String?
    var contentCredits: String?
    var font: String?
    
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        contentText         <- map["content_text"]
        contentTitle         <- map["content_title"]
        contentCredits         <- map["content_credits"]
        font         <- map["font"]
    }
}
