//
//  File.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import ObjectMapper

struct ReviewPerson: BaseModel {
    var author : String?
    var content : String?
    var id : String?
    var url : String?
}
