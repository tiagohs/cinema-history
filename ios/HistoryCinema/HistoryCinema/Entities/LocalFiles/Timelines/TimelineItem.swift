//
//  TimelineItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class TimelineItem: Timeline {
    var year: String!
    var description: String!
    var imageTransparent: Bool!
    var title: String?
    var marginTop: Int?
    var image: LocalImage?
    var imageInfo: ContentInformation?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        year                                    <- map["year"]
        description                             <- map["description"]
        imageTransparent                        <- map["image_transparent"]
        title                                   <- map["title"]
        marginTop                               <- map["margin_top"]
        image                                   <- map["image"]
        imageInfo                               <- map["image_info"]
    }
}
