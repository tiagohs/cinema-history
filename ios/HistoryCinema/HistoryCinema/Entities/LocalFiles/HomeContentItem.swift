//
//  HomeContentItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import ObjectMapper

class HomeContentItem: BaseLocalModel {
    var mainTopicType: MainTopicsType!
    var image: LocalImage?
    var darkMode: Bool?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        mainTopicType                            <- (map["main_topic_type"], EnumTransform<MainTopicsType>())
        image                                   <- map["image"]
        darkMode                                <- map["dark_mode"]
    }
    
    static func example(_ type: MainTopicsType) -> HomeContentItem {
        return HomeContentItem(JSON: [
            "main_topic_type": type.rawValue,
            "dark_mode": true,
            "image": [
                "image_type": "local",
                "url": "img_chinatown",
                "content_description": "Poster do filme Chinatown, com o personagem de costas.",
                "style": [
                    "scale_type": "center_crop"
                ]
            ]
    ])!
    }
}
