//
//  Animation.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

struct Animation: BaseModel {
    var id: String? = UUID().uuidString
    var type: AnimationType
    var duration: Int
}
