//
//  Image.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

struct LocalImage: BaseModel {
    var id: String? = UUID().uuidString
    var imageType: ImageType?
    var url: String?
    var contentDescription: String?
    var animation: Animation?
    var imageStyle: ImageStyle?
    
    enum CodingKeys: String, CodingKey {
        case id, animation
        case imageStyle = "style"
        case imageType = "image_type"
        case contentDescription = "content_description"
    }
}
