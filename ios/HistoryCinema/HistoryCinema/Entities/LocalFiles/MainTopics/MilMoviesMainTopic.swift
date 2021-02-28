//
//  MilMoviesMainTopic.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class MilMoviesMainTopic: MainTopic {
    var id: Int!
    var listId: String!
    var title: String!
    var backgroundColor: String?
    var titleColor: String?
    var image: LocalImage!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        id                                              <- map["id"]
        listId                                          <- map["list_id"]
        title                                           <- map["title"]
        backgroundColor                                 <- map["background_color"]
        image                                           <- map["image"]
        titleColor                                      <- map["title_color"]
    }
}
