//
//  ImageStyle.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

struct ImageStyle: BaseModel {
    var id: String? = UUID().uuidString
    var width: Int?
    var height: Int?
    var resize: ImageResize?
    var scaleType: String?
    
    enum CodingKeys: String, CodingKey {
        case width, height, resize
        case scaleType = "scale_type"
    }
}
