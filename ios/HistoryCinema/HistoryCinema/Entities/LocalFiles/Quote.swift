//
//  Quote.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class Quote: BaseLocalModel {
    var id: Int!
    var quote: String!
    var author: String!
    var iconColor: String?
    var backgroundColor: String?
    var textColor: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        id                                                  <- map["id"]
        quote                                               <- map["quote"]
        author                                              <- map["author"]
        iconColor                                          <- map["icon_color"]
        backgroundColor                                    <- map["background_color"]
        textColor                                          <- map["text_color"]
    }
    
    static let example = Quote(JSONString: "{\n        \"id\": 2,\n        \"quote\": \"Hoje mais que nunca precisamos ouvir uns aos outros e entender como vemos o mundo, e o cinema é o melhor meio de fazer isso.\",\n        \"author\": \"Martin Scorsese\",\n        \"icon_color\": \"md_white_1000\"\n    }")!
}
