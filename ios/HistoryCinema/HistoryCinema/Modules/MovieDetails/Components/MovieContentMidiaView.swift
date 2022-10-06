//
//  MovieContentMidiaView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct MovieContentMidiaView: View {
    let movie: Movie!
    
    var body: some View {
        let allImages = movie.allImages()
        let allVideos = movie.videos?.results ?? []
        
        if allImages.isEmpty && allVideos.isEmpty {
            AnyView(EmptyView())
        } else {
            ContentContainer {
                VStack(alignment: .leading) {
                    Text("Mídia")
                        .font(.oswaldRegular(size: 24))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 5)
                        .padding(.leading, 20)
                    
                    if !allVideos.isEmpty {
                        VideoListView(videoList: allVideos)
                    }
                    
                    if !allImages.isEmpty {
                        ImageListView(images: allImages)
                    }
                }
                .padding(.leading, 12)
                .padding(.vertical, 20)
            }
        }
    }
}

struct MovieContentMidiaView_Previews: PreviewProvider {
    static var previews: some View {
        MovieContentMidiaView(movie: Movie.exampleMovieFull())
    }
}
