//
//  ContentNominee.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentNominee: Content {
    var name: String?
    var year: String?
    var nomineeList: [Nominee]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        name                                                <- map["name"]
        year                                                <- map["year"]
        nomineeList                                         <- map["nominee_list"]
    }
}
