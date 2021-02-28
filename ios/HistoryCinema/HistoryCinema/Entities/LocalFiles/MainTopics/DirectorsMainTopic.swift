//
//  DirectorsMainTopic.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class DirectorsMainTopic: MainTopic {
    var personId: Int!
    var title: String!
    var image: LocalImage!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        personId                                                <- map["person_id"]
        title                                                   <- map["title"]
        image                                                   <- map["image"]
    }
}
