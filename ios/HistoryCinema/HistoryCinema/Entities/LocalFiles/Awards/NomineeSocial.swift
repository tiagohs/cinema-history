//
//  NomineeSocial.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class NomineeSocial: BaseLocalModel {
    var type: SocialType!
    var link: String?
    
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        type                                    <- (map["type"], EnumTransform<SocialType>())
        link                                    <- map["link"]
    }
}
