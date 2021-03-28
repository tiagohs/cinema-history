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
        super.mapping(map: map)
        
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
    
    static let exampleNomineeMovie = Nominee(JSONString: "{\n                  \"type\": \"movie\",\n                  \"id\": 496243,\n                  \"name\": \"Parasita\",\n                  \"image_path\": \"\\/igw938inb6Fy0YVcwIyxQ7Lu5FO.jpg\",\n                  \"director\": \"Bong Joon-ho\",\n                  \"winner\": true\n                }")!
    
    static let exampleNomineePerson = Nominee(JSONString: "{\n                  \"type\": \"person\",\n                  \"id\": 73421,\n                  \"image_path\": \"\\/nXMzvVF6xR3OXOedozfOcoA20xh.jpg\",\n                  \"name\": \"Joaquin Phoenix\",\n                  \"winner\": false,\n                  \"movie\": {\n                    \"type\": \"movie\",\n                    \"id\": 475557,\n                    \"name\": \"Coringa\",\n                    \"image_path\": \"\\/xLxgVxFWvb9hhUyCDDXxRPPnFck.jpg\",\n                    \"director\": \"Todd Phillips\"\n                  }\n                }")!
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
