//
//  ImageStyle.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import ObjectMapper

class ImageStyle: BaseLocalModel {
    var width: Int?
    var height: Int?
    var resize: ImageResize?
    var scaleType: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        width       <- map["width"]
        height      <- map["height"]
        resize      <- map["resize"]
        scaleType   <- map["scale_type"]
    }
}

