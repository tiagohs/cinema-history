//
//  MainTopicItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class MainTopicItem: MainTopic {
    var id: Int!
    var title: String!
    var subtitle: String!
    var description: String!
    var image: LocalImage!
    var blocked: Bool!
    var isNew: Bool!
    var presentationImage: LocalImage?
    var titleColor: String?
    var titleBackgroundColor: String?
    var color: String?
    var quotePosition: ViewPosition!
    var quote: Quote!
    var sumarioList: [SummaryModel]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        quotePosition                               <- (map["quote_position"], EnumTransform<ViewPosition>())
        id                                          <- map["id"]
        title                                       <- map["title"]
        subtitle                                    <- map["subtitle"]
        description                                 <- map["description"]
        image                                       <- map["image"]
        blocked                                     <- map["blocked"]
        isNew                                       <- map["is_new"]
        presentationImage                           <- map["presentation_image"]
        titleColor                                  <- map["title_color"]
        titleBackgroundColor                        <- map["title_background_color"]
        color                                       <- map["color"]
        quotePosition                               <- map["quote_position"]
        quote                                       <- map["quote"]
    }
}
