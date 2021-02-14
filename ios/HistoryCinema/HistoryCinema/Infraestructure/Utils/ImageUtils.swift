//
//  ImageUtils.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 13/02/21.
//

import Foundation

class ImageUtils {
    
    static func formatImageUrl(imageID: String?, imageSize: String) -> String? {
        guard let id = imageID else {
            return nil
        }
        
        return "\(TMDB.BASE_IMAGE_URL)\(imageSize)/\(id)"
    }
}
