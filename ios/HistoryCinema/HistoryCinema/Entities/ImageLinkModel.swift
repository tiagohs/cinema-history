//
//  ImageLinkModel.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import Foundation

class ImageLinkModel: Identifiable {
    let index: Int
    let images: [ImageDTO]
    
    init(_ index: Int, _ images: [ImageDTO]) {
        self.index = index
        self.images = images
    }
}

