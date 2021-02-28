//
//  ContentVideo.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentVideo: Content {
    var videoId: String!
    var height: Int?
    var information: ContentInformation!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        videoId         <- map["video_id"]
        height         <- map["height"]
        information         <- map["information"]
    }
}
