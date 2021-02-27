//
//  MovieExtraInfo.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class MovieExtraInfo: BaseLocalModel {
    var id : Int!
    var reviewResults : [ReviewsResult]?
    var quote : ContentQuote?
    var watchOn : [LocalNetwork]?
    var didYouKnowList : [DidYouKnow]?
    var blockSpecial : ContentBlockSpecial?

    var milMoviesMainTopic: MilMoviesMainTopic?
    var historyMainTopic: MainTopicItem?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        id                                                      <- map["id"]
        reviewResults                                           <- map["review_results"]
        quote                                                   <- map["title"]
        watchOn                                                 <- map["watchOn"]
        didYouKnowList                                          <- map["did_you_know_list"]
        blockSpecial                                            <- map["block_special"]
    }
}
