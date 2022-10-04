//
//  MovieExtraInfoResult.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class ExtraInfoResult: BaseLocalModel {
    var results : [MovieExtraInfoResult]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        results                                              <- map["results"]
    }
}

class MovieExtraInfoResult: BaseLocalModel {
    var list : String?
    var milMoviesMainTopicID : Int!
    var historyMainTopicID : Int!
    var movies : [MovieExtraInfo]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        list                                              <- map["list"]
        milMoviesMainTopicID                              <- map["milMoviesMainTopicID"]
        historyMainTopicID                                <- map["historyMainTopicID"]
        movies                                            <- map["movies"]
    }
}
