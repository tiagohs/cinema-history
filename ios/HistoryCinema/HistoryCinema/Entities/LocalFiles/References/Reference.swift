//
//  Reference.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation
import ObjectMapper

class Reference: BaseLocalModel {
    var type: ReferenceType!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        type                                                    <- (map["type"], EnumTransform<ReferenceType>())
    }
    
    static func getReference(from dictionary: Dictionary<String, Any>, by referenceType: ReferenceType) throws -> Reference? {
        switch referenceType {
        case .text:
            return ReferenceText(JSON: dictionary)
        case .media:
            return ReferenceMedia(JSON: dictionary)
        }
    }
}

class ReferenceContent: BaseLocalModel {
    var name: String!
    var references: [Reference]?

    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        name                                                    <- map["name"]
        references                                              <- map["references"]
    }
}
