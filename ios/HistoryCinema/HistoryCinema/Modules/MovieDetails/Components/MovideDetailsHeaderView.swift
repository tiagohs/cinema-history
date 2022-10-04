//
//  MovideDetailsHeaderView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 30/09/22.
//

import SwiftUI

struct HeaderLeftView: View {
    let movie: Movie!
    
    var body: some View {
        VStack(alignment: .leading) {
            if let title = movie.title {
                Text(title)
                    .font(.oswaldBold(size: 22))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.white)
            }
            
            if let originalTitle = movie.originalTitleWithYear() {
                Text(originalTitle)
                    .font(.proximaNovaRegular(size: 16))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.white)
            }
            
            if let genres = movie.genres {
                if !genres.isEmpty {
                    HStack {
                        ForEach(Array(genres.enumerated()), id: \.offset) { index, genre in
                            if (index <= 2) {
                                Text(genre.name ?? "")
                                    .font(.oswaldBold(size: 12))
                                    .multilineTextAlignment(.center)
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 16)
                                    .padding(.vertical, 8)
                                    .background(.black)
                                    .cornerRadius(20)
                            }
                        }
                    }
                }
            }
        }
        .padding()
        .layoutPriority(100)
    }
}

struct HeaderRightView: View {
    let movie: Movie!
    
    @State private var showVideoPlayer = false
    
    var body: some View {
        VStack {
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
            .padding(35)
        }
        .onTapGesture {
            if movie.trailerKey() != nil {
                showVideoPlayer.toggle()
            }
        }
        .sheet(isPresented: $showVideoPlayer) {
            VideoViewerView(youtubeVideoId: movie.trailerKey())
        }
    }
}

struct MovideDetailsHeaderView: View {
    let movie: Movie!
    
    var body: some View {
        let backdropImageUrl = ImageUtils.formatImageUrl(imageID: movie.backdropPath, imageSize: TMDB.ImageSize.BACKDROP.w780) ?? ""
        
        ZStack(alignment: Alignment(horizontal: .leading, vertical: .bottom)) {
            CustomImage(
                imageUrl: backdropImageUrl,
                imageType: .online,
                placeholderType: .movie,
                height: 350
            )
                .frame(height: 350)
            
            Rectangle()
                .background(.black)
                .opacity(0.2)
            
            HStack {
                HeaderLeftView(movie: movie)
                
                Spacer()
                
                HeaderRightView(movie: movie)
            }
            .padding(.bottom, 10)
        }
        .frame(height: 350)
    }
}

struct MovideDetailsHeaderView_Previews: PreviewProvider {
    static var previews: some View {
        MovideDetailsHeaderView(movie: Movie.exampleMovieFull())
    }
}
