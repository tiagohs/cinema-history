//
//  ReferenceMedia.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation
import ObjectMapper

class ReferenceMedia: Reference {
    var title: String!
    var subtitle: String!
    var description: String!
    var image: LocalImage!
    var mediaType: String!
    var link: String!
    var buttonText: String!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        title                                                       <- map["title"]
        subtitle                                                    <- map["subtitle"]
        description                                                 <- map["description"]
        image                                                       <- map["image"]
        mediaType                                                   <- map["mediaType"]
        link                                                        <- map["link"]
        buttonText                                                  <- map["buttonText"]
    }
}
