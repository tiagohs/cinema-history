//
//  ContentImage.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentImage: Content {
    var image: LocalImage!
    var height: Int?
    var information: ContentInformation!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        image                                           <- map["image"]
        height                                          <- map["height"]
        information                                     <- map["information"]
    }
}
