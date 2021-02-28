//
//  AwardMainTopic.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class AwardMainTopic: MainTopic {
    var id: Int!
    var name: String?
    var country: String?
    var presentedBy: String?
    var firstAwardedDate: String?
    var image: LocalImage!
    var logo: LocalImage!
    var nomineesId: Int?
    var socialList: [Social]?
    
    var history: [Content]?
    var nomineesList: [NomineeResult]?
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        id                                                  <- map["id"]
        name                                                <- map["name"]
        country                                             <- map["country"]
        presentedBy                                         <- map["presented_by"]
        firstAwardedDate                                    <- map["first_awarded_date"]
        image                                               <- map["image"]
        logo                                                <- map["logo"]
        nomineesId                                          <- map["nominees_id"]
        socialList                                          <- map["social_list"]
    }
}
