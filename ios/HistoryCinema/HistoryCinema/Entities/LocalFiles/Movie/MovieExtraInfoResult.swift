//
//  MovieExtraInfoResult.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class MovieExtraInfoResult: BaseLocalModel {
    var list : String?
    var milMoviesMainTopicID : Int!
    var historyMainTopicID : Int!
    var movies : [MovieExtraInfo]?
}
