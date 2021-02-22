//
//  ContentSlide.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentSlide: Content {
    var images: [LocalImage]!
    var height: Int?
    var information: ContentInformation!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        images         <- map["images"]
        height         <- map["height"]
        information         <- map["information"]
    }
}
