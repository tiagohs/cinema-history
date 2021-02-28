//
//  ContentMovieListSpecial.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentMovieListSpecial: Content {
    var movies: [LocalMovie]?
    var information: ContentInformation!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        movies                                           <- map["movies"]
        information                                      <- map["information"]
    }
}
