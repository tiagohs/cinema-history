//
//  ContentVideoView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct ContentVideoView: View {
    let contentVideo: ContentVideo
    
    @State var showVideoContent: Bool = false
    
    var body: some View {
        let height = contentVideo.height ?? 250
        
        VStack {
            if showVideoContent {
                VideoPlayerView(youtubeVideoId: contentVideo.videoId, autoplay: true)
                    .frame(height: CGFloat(height), alignment: .center)
            } else {
                ZStack {
                    CustomImage(
                        imageUrl: contentVideo.videoId.toYoutubeUrl()!,
                        imageType: .online,
                        placeholderType: .movie,
                        height: height
                    )
                    
                    Button(action: {
                        showVideoContent = true
                    }) {
                        Image(systemName: "play")
                            .padding()
                            .background(Color.black)
                            .foregroundColor(Color.white)
                            .clipShape(Circle())
                    }
                    .shadow(color: .black, radius: 5)
                    .padding()
                    
                }
                .frame(height: CGFloat(height), alignment: .center)
            }
            
            ContentInformationView(contentInformation: contentVideo.information)
        }
        .onDisappear { showVideoContent = false }
        .padding(.vertical, 24)
    }
}

struct ContentVideoView_Previews: PreviewProvider {
    static var previews: some View {
        ContentVideoView(contentVideo: Content.exampleVideo)
        ContentVideoView(contentVideo: Content.exampleVideo)
            .preferredColorScheme(.dark)
    }
}
