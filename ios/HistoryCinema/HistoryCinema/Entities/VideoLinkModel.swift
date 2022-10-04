//
//  VideoLinkModel.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import Foundation

class VideoLinkModel: Identifiable {
    let youtubeKey: String
    
    init(_ youtubeKey: String) {
        self.youtubeKey = youtubeKey
    }
}

