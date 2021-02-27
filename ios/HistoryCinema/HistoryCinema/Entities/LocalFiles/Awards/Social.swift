//
//  Social.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class Social: BaseLocalModel {
    var type: SocialType!
    var link: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        type                                          <- (map["quote_position"], EnumTransform<SocialType>())
        link                                          <- map["link"]
    }
}
