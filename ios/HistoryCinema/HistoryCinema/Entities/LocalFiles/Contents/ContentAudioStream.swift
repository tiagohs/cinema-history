//
//  ContentAudioStream.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentAudioStream: Content {
    var path: String!
    var image: LocalImage!
    var information: ContentInformation!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        type <- (map["type"], EnumTransform<ContentType>())
    }
}

    
