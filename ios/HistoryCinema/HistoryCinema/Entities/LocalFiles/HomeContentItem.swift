//
//  HomeContentItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

struct HomeContentItem: BaseModel {
    var id: String? = UUID().uuidString
    var mainTopicType: MainTopicsType?
    var image: LocalImage?
    var darkMode: Bool?
    
    enum CodingKeys: String, CodingKey {
        case image
        case mainTopicType = "main_topic_type"
        case darkMode = "dark_mode"
    }
}
