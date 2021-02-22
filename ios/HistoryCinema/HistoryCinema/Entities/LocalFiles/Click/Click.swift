//
//  Click.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class Click: BaseLocalModel {
    var screen: Screen?
    var buttonText: String?
    var parameters: [ScreenParameter]?

    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        screen                                          <- (map["screen"], EnumTransform<Screen>())
        buttonText                                      <- map["button_text"]
        parameters                                      <- map["parameters"]
    }
}
