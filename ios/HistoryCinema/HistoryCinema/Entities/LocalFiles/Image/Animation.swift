//
//  Animation.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import ObjectMapper

class Animation: BaseLocalModel {
    var type: AnimationType!
    var duration: Int!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        type                            <- (map["type"], EnumTransform<AnimationType>())
        duration                        <- map["duration"]
    }
}
