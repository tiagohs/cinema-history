//
//  Image.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import ObjectMapper

class LocalImage: BaseLocalModel {
    var imageType: ImageType?
    var url: String?
    var contentDescription: String?
    var animation: Animation?
    var imageStyle: ImageStyle?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        imageType               <- (map["image_type"], EnumTransform<ImageType>())
        url                     <- map["url"]
        contentDescription       <- map["content_description"]
        animation               <- map["animation"]
        imageStyle              <- map["style"]
    }
}
