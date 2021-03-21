//
//  Summary.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/02/21.
//

import Foundation
import ObjectMapper

class SummaryModel: BaseLocalModel {
    var id: Int!
    var title: String!
    var description: String!
    var image: LocalImage!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        id                                                          <- map["id"]
        title                                                       <- map["title"]
        description                                                 <- map["description"]
        image                                                       <- map["image"]
    }
    
    static let example = SummaryModel(JSONString: "{\n          \"id\": 1,\n          \"title\": \"Visionários\",\n          \"description\": \"Os primeiros inventores, sonhadores e artistas. Da invenção do cinetoscópio até a primeira exibição conhecida pública de um filme para um público pagante pelos irmãos Lumierè.\",\n          \"image\": {\n            \"image_type\": \"local\",\n            \"url\": \"img_great_train\",\n            \"style\": {\n              \"scale_type\": \"center_crop\",\n              \"resize\": {\n                \"height\": 350\n              }\n            }\n          }\n        }")!
}
