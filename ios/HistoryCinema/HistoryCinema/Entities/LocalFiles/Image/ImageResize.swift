//
//  ImageResize.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

struct ImageResize: BaseModel {
    var id: String? = UUID().uuidString
    var width: Int?
    var height: Int?
}
