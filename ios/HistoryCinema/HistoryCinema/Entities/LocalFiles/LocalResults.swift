//
//  Results.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class NomineeResult: BaseLocalModel  {
    var year: String?
    var content: [Content]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        year                                              <- map["year"]
        content                                           <- map["content"]
    }
}
