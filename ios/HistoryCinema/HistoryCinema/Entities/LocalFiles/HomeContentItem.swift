//
//  HomeContentItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import ObjectMapper

class HomeContentItem: BaseLocalModel {
    var mainTopicType: MainTopicsType?
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
}

class HomeContentResult: BaseLocalModel {
    var results: [HomeContentItem]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        results                                   <- map["results"]
    }
}
