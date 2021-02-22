//
//  BaseLocalModel.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class BaseLocalModel: Mappable {
    
    init() {}
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
    }
}
