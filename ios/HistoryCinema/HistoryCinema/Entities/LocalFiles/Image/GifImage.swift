//
//  GifImage.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class GifImage: BaseLocalModel {
    var imageType: ImageType!
    var url: String!
    var thumbnail: LocalImage!
    var imageStyle: ImageStyle?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        imageType                               <- (map["image_type"], EnumTransform<ImageType>())
        imageStyle                              <- map["style"]
        url                                     <- map["url"]
        thumbnail                               <- map["thumbnail"]
    }
}
