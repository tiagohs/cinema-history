//
//  Review.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation
import ObjectMapper

struct Review: BaseModel {
    var id: Int? = UUID().hashValue
    var page : Int?
    var reviewsList : [ReviewPerson]?
    var totalPages : Int?
    var totalResults : Int?
    
    enum CodingKeys: String, CodingKey {
        case page
        case reviewsList = "results"
        case totalPages = "total_pages"
        case totalResults = "total_results"
    }
}
