//
//  Release.swift
//  popmovies
//
//  Created by Tiago Silva on 18/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct Release: BaseModel {
    var id: Int? = UUID().hashValue
    var countries : [ReleaseCountry]?
}
