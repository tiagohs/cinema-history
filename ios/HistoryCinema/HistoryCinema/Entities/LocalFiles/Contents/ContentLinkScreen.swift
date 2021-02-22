//
//  ContentLinkScreen.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentLinkScreen: Content {
    var title: String?
    var subtitle: String?
    var description: String!
    var image: LocalImage?
    var click: Click?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        title                                           <- map["title"]
        subtitle                                        <- map["subtitle"]
        description                                     <- map["description"]
        image                                           <- map["image"]
        click                                           <- map["click"]
    }
}
