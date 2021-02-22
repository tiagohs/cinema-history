//
//  ImageResize.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import ObjectMapper

class ImageResize: BaseLocalModel {
    var width: Int?
    var height: Int?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        width       <- map["width"]
        height      <- map["height"]
    }
}
