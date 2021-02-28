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
        super.mapping(map: map)
        
        quote   <- map["quote"]
    }
    
    static let example = QuoteMainTopic(JSONString: "{\n    \"main_topic_type\": \"\",\n    \"layout_type\": \"quote\",\n    \"quote\": {\n        \"id\": 2,\n        \"quote\": \"Hoje mais que nunca precisamos ouvir uns aos outros e entender como vemos o mundo, e o cinema é o melhor meio de fazer isso.\",\n        \"author\": \"Martin Scorsese\",\n        \"icon_color\": \"md_white_1000\"\n    }\n  }")!
}
