//
//  Image+Extensions.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import Foundation
import SwiftUI

extension Image {
    
    static func load(from localImage: LocalImage?) -> Image {
        let imageComponent = Image(localImage?.url ?? "")
        let imageHeight = CGFloat(localImage?.imageStyle?.resize?.height ?? 400)
        
        if let imageScaleType = localImage?.imageStyle?.scaleType {
            if imageScaleType == "center_crop" {
                imageComponent
                    .resizable()
                    .scaledToFill()
                    .frame(height: imageHeight)
            } else {
                imageComponent
                    .resizable()
                    .scaledToFill()
                    .frame(height: imageHeight)
            }
        } else {
            imageComponent
                .frame(height: imageHeight)
        }
        
        return imageComponent
    }
}
