//
//  QuoteMainTopic.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class QuoteMainTopic: MainTopic {
    var quote: Quote!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        quote   <- map["quote"]
    }
}
