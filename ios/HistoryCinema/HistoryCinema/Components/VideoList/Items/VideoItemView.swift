//
//  VideoItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct VideoItemView: View {
    let video: Video!
    let onVideoClick: (Video) -> Void
    
    var body: some View {
        let imageHeight = 230
        
        if let videoKey = video.key, !videoKey.isEmpty {
            let url = "https://img.youtube.com/vi/\(videoKey)/0.jpg"
            
            ZStack(alignment: Alignment(horizontal: .center, vertical: .center)) {
                SmoothAsyncImageView(url: URL(string: url)) { phase in
                    if let image = phase.image {
                        image
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                    } else if phase.error != nil {
                        Placeholder(type: .movie, iconSize: CGFloat(100))
                            .cornerRadius(CGFloat(10))
                            .frame(
                                height: CGFloat(imageHeight)
                            )
                    } else {
                        Placeholder(type: .movie, iconSize: CGFloat(100))
                            .cornerRadius(CGFloat(10))
                            .frame(
                                height: CGFloat(imageHeight)
                            )
                    }
                }
                .frame(width: 320, height: CGFloat(imageHeight))
                .cornerRadius(10)
                .shadow(radius: 5)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color(.sRGB, red: 150/255, green: 150/255, blue: 150/255, opacity: 0.1), lineWidth: 1)
                )
                
                ZStack {
                    Circle()
                        .background(Color.backgroundInverse)
                        .opacity(0.7)
                        .clipShape(Circle())
                        
                    Image(systemName: "play.fill")
                        .font(.system(size: 26, weight: .bold))
                        .padding()
                        .foregroundColor(Color.textInverse)
                        .clipShape(Circle())
                }
                .frame(width: 50, height: 50)
            }
            .onTapGesture {
                onVideoClick(video)
            }
        } else {
            AnyView(EmptyView())
        }
    }
}

struct VideoItemView_Previews: PreviewProvider {
    static var previews: some View {
        VideoItemView(video: Movie.exampleMovieFull().videos?.results![0]) { video in
            
        }
    }
}
