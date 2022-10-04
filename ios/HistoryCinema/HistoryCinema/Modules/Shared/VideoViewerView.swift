//
//  VideoViewerView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 30/09/22.
//

import SwiftUI

struct VideoViewerView: View {
    let youtubeVideoId: String?
    
    var body: some View {
        VStack {
            VideoPlayerView(youtubeVideoId: youtubeVideoId, autoplay: true)
                .frame(height: 300, alignment: .center)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.black)
    }
}

struct VideoViewerView_Previews: PreviewProvider {
    static var previews: some View {
        VideoViewerView(youtubeVideoId: "sfI0NVC0hLU")
    }
}
