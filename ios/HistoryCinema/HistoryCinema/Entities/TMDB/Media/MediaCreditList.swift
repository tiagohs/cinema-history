//
//  MediaCreditList.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation
import ObjectMapper

struct MediaCreditList: BaseModel {
    var id: Int? = UUID().hashValue
    var cast : [MediaCreditCast]?
    var crew : [MediaCreditCrew]?
}
