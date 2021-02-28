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
        switch type {
        case .awards:
            return HomeContentItem(JSONString: "{\n          \"main_topic_type\": \"awards\",\n          \"dark_mode\": true,\n          \"image\": {\n            \"image_type\": \"local\",\n            \"url\": \"img_awards\",\n            \"style\": {\n              \"scale_type\": \"center_crop\"\n            }\n          }\n        }")!
        case .directors:
            return HomeContentItem(JSONString: "{\n          \"main_topic_type\": \"directors\",\n          \"dark_mode\": false,\n          \"image\": {\n            \"image_type\": \"local\",\n            \"url\": \"img_david_fincher\",\n            \"content_description\": \"Poster personalizado com o rosto de david fincher, misturado a diversos personagens dos seus filmes.\",\n            \"style\": {\n              \"scale_type\": \"center_crop\"\n            }\n          }\n        }")!
        case .history_cinema:
            return HomeContentItem(JSONString: "{\n          \"main_topic_type\": \"history_cinema\",\n          \"dark_mode\": true,\n          \"image\": {\n            \"image_type\": \"local\",\n            \"url\": \"img_chinatown\",\n            \"content_description\": \"Poster do filme Chinatown, com o personagem de costas.\",\n            \"style\": {\n              \"scale_type\": \"center_crop\"\n            }\n          }\n        }")!
        case .mil_movies:
            return HomeContentItem(JSONString: "{\n          \"main_topic_type\": \"mil_movies\",\n          \"dark_mode\": false,\n          \"image\": {\n            \"image_type\": \"local\",\n            \"url\": \"img_godfather\",\n            \"content_description\": \"Poster do filme o poderoso chefão, com uma mão segurando um controlador de marionete de corda.\",\n            \"style\": {\n              \"scale_type\": \"center_crop\"\n            }\n          }\n        }")!
        case .timeline:
            return HomeContentItem(JSONString: "{\n          \"main_topic_type\": \"timeline\",\n          \"dark_mode\": false,\n          \"image\": {\n            \"image_type\": \"local\",\n            \"url\": \"img_timeline\",\n            \"content_description\": \"Poster do filme Chinatown, com o personagem de costas.\",\n            \"style\": {\n              \"scale_type\": \"center_crop\"\n            }\n          }\n        }")!
        }
    }
}
