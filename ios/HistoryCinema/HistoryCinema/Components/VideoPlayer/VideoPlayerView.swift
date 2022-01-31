//
//  VideoPlayerView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct VideoPlayerView: View {
    var youtubeVideoId: String?
    @State var loadingFinished: Bool = false

    var body: some View {
        if youtubeVideoId != nil {
            ZStack {
                ProgressView("Carregando")
                
                YouTubeView(videoId: youtubeVideoId!)
                    
            }
            .frame(alignment: .center)
        } else {
            EmptyView()
        }
    }

}

struct VideoPlayerView_Previews: PreviewProvider {
    static var previews: some View {
        VideoPlayerView(youtubeVideoId: "sfI0NVC0hLU")
    }
}
