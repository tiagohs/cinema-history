//
//  LocalNetwork.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class LocalNetwork: BaseLocalModel {
    var type : NetworkType!
    var name : String?
    var link : String?
    
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        type                                            <- (map["type"], EnumTransform<NetworkType>())
        name                                            <- map["name"]
        link                                            <- map["link"]
    }
}
