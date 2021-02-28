//
//  ContentQuote.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentQuote: Content {
    var quote: Quote!
    var quoteMarkColor: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        quote                                               <- map["quote"]
        quoteMarkColor                                      <- map["quote_mark_color"]
    }
}
