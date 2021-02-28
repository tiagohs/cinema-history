//
//  ContentGif.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentGif: Content {
    var gifImage: GifImage!
    var information: ContentInformation!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        gifImage                                        <- map["gif_image"]
        information                                     <- map["information"]
    }
}
