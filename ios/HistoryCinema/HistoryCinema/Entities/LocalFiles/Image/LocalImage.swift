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
    
    static var example = LocalImage(JSONString: "{\n            \"image_type\": \"local\",\n            \"url\": \"img_great_train\",\n            \"style\": {\n              \"scale_type\": \"center_crop\",\n              \"resize\": {\n                \"height\": 350\n              }\n            }\n          }")
}
