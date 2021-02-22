//
//  ContentInformation.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class ContentInformation: BaseLocalModel {
    var contentText: String?
    var contentTitle: String?
    var source: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        contentText         <- map["contentText"]
        contentTitle        <- map["contentTitle"]
        source              <- map["source"]
    }
}
