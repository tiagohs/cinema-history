//
//  YouTubeView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import Foundation
import SwiftUI
import UIKit

// 1
struct YouTubeView: UIViewRepresentable {
    var videoId: String
    var autoplay: Bool = false
    
    // 5
    func makeUIView(context: Context) -> YouTubePlayerView {
        let playerVars = [
                    "controls": "2",
                    "fs": "0",
                    "hl": Locale.current.languageCode ?? "en",
                    "iv_load_policy": "3",
                    "modestbranding": "1",
                    "rel" : "0",
                    "playsinline": "1",
                    "autoplay": autoplay ? "1" : "0"
                ] as YouTubePlayerView.YouTubePlayerParameters

        let playerView = YouTubePlayerView()
        playerView.playerVars = playerVars
        
        return playerView
    }
      
    // 6
    func updateUIView(_ uiView: UIViewType, context: Context) {
        uiView.loadVideoID(videoId)
    }
}
