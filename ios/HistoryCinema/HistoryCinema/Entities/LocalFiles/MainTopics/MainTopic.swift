//
//  MainTopic.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class MainTopic: BaseLocalModel {
    var layoutType: MainTopicItemLayoutType!
    var mainTopicType: MainTopicsType!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        layoutType                               <- (map["layout_type"], EnumTransform<MainTopicItemLayoutType>())
        mainTopicType                            <- (map["main_topic_type"], EnumTransform<MainTopicsType>())
    }
    
    static func getMainTopic(from dictionary: Dictionary<String, Any>, by mainTopicsType: MainTopicsType) throws -> MainTopic? {
        switch mainTopicsType {
        case .awards:
            return AwardMainTopic(JSON: dictionary)
        case .directors:
            return DirectorsMainTopic(JSON: dictionary)
        case .history_cinema:
            return MainTopicItem(JSON: dictionary)
        case .mil_movies:
            return MilMoviesMainTopic(JSON: dictionary)
        case .timeline:
            return MainTopic(JSON: dictionary)
        }
    }
}
