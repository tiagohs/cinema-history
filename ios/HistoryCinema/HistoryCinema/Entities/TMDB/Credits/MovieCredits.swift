//
//  MovieCredits.swift
//  popmovies
//
//  Created by Tiago Silva on 19/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct MovieCredits: BaseModel {
    var id: Int? = UUID().hashValue
    var cast : [CreditCast]?
    var crew : [CreditCrew]?
}
