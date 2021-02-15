//
//  Results.swift
//  popmovies
//
//  Created by Tiago Silva on 14/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct ResultsMovie: BaseModel {
    var id: String? = UUID().uuidString
    var page : Int?
    var total_results : Int?
    var total_pages : Int?
    var results : [Movie]?
}

struct ResultsVideo: BaseModel {
    var id: String? = UUID().uuidString
    var page : Int?
    var total_results : Int?
    var total_pages : Int?
    var results : [Video]?
}

struct ResultsPerson: BaseModel {
    var id: String? = UUID().uuidString
    var page : Int?
    var total_results : Int?
    var total_pages : Int?
    var results : [Person]?
}

struct ResultsKeyword: BaseModel {
    var id: Int? = UUID().hashValue
    var keywords : [Keyword]?
}

struct TranslationResults: BaseModel {
    var id: Int? = UUID().hashValue
    var translations : [Translation]?
}

struct ImageResults: BaseModel {
    var id: Int?
    var profiles: [Image]?
}

struct VideoResult: BaseModel {
    var id: Int? = UUID().hashValue
    var results : [Video]?
}
