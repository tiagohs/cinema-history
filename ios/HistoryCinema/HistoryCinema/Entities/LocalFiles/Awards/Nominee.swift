//
//  Nominee.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class Nominee: BaseLocalModel {
    var type: NomineeType!
    var id: Int?
    var name: String?
    var imagePath: String?
    var winner: Bool?
    var movie: Nominee?
    var department: String?
    var country: String?
    var director: String?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        type                                                    <- (map["type"], EnumTransform<NomineeType>())
        id                                                      <- map["id"]
        name                                                    <- map["name"]
        imagePath                                               <- map["image_path"]
        winner                                                  <- map["winner"]
        movie                                                   <- map["movie"]
        department                                              <- map["department"]
        country                                                 <- map["country"]
        director                                                <- map["director"]
    }
}

class NomineeContent: BaseLocalModel {
    var year: String?
    var content: [Content]!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        year                                              <- map["year"]
        content                                           <- map["content"]
    }
}
