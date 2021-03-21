//
//  ContentVideoView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct ContentVideoView: View {
    let contentVideo: ContentVideo
    
    var body: some View {
        let height = contentVideo.height ?? 250
        
        VStack {
            VideoPlayerView(youtubeVideoId: contentVideo.videoId)
                .frame(height: CGFloat(height), alignment: .center)
            ContentInformationView(contentInformation: contentVideo.information)
        }
        .padding(.vertical, 16)
    }
}

struct ContentVideoView_Previews: PreviewProvider {
    static var previews: some View {
        ContentVideoView(contentVideo: Content.exampleVideo)
    }
}
