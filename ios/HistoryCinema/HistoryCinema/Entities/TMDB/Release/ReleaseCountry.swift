//
//  ReleaseCountry.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation
import ObjectMapper

struct ReleaseCountry: BaseModel {
    var id: Int? = UUID().hashValue
    var certification : String?
    var country : String?
    var primary : Bool?
    var release_date : String?
    
    enum CodingKeys: String, CodingKey {
        case certification, primary
        case country = "iso_3166_1"
        case release_date = "release_date"
    }
}
