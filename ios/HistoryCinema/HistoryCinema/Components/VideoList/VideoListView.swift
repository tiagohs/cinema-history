//
//  VideoListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct VideoListView: View {
    let videoList: [Video]!
    
    @State var videoLinkModel: VideoLinkModel? = nil
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .top, spacing: 0) {
                ForEach(0 ..< videoList.count) { index in
                    let video = videoList[index]
                    
                    VideoItemView(video: video) { video in
                        if let videoKey = video.key {
                            self.videoLinkModel = VideoLinkModel(videoKey)
                        }
                    }
                        .padding(.leading, index == 0 ? 20 : 8)
                }
            }
        }
        .sheet(item: $videoLinkModel) { videoLinkModel in
            VideoViewerView(youtubeVideoId: videoLinkModel.youtubeKey)
        }
    }
}

struct VideoListView_Previews: PreviewProvider {
    static var previews: some View {
        VideoListView(videoList: Movie.exampleMovieFull().videos?.results ?? [])
    }
}
