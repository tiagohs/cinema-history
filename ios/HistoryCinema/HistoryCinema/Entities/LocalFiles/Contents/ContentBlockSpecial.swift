//
//  ContentBlockSpecial.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentBlockSpecial: Content {
    var title: String?
    var description: String!
    var credits: String?
    var image: LocalImage?
    var click: Click?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        title                                               <- map["title"]
        description                                         <- map["description"]
        credits                                             <- map["credits"]
        image                                               <- map["image"]
        click                                               <- map["click"]
    }
}
