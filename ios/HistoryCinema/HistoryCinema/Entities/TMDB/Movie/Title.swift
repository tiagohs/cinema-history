//
//  Title.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation
import ObjectMapper

struct Title: BaseModel {
    var id: Int? = UUID().hashValue
    var country : String?
    var title : String?
    var type : String?
    
    enum CodingKeys: String, CodingKey {
        case title, type
        case country = "iso_3166_1"
    }
}

