//
//  MovieItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import SwiftUI
import Kingfisher

struct MovieItemView: View {
    let movie: LocalMovie
    var posterWidth: Int? = nil
    var posterHeight: Int? = nil
    
    var body: some View {
        let width = posterWidth ?? 200
        let height = posterHeight ?? 280
        let movie = movie
        let imageUrl = URL(string: ImageUtils.formatImageUrl(imageID: movie.posterPath, imageSize: TMDB.ImageSize.POSTER.w342) ?? "")
            
        ZStack(alignment: Alignment(horizontal: .center, vertical: .bottom)) {
                KFImage.url(imageUrl)
                    .placeholder {
                        Placeholder(type: .movie, iconSize: CGFloat(100))
                            .cornerRadius(20)
                            .frame(
                                width: CGFloat(width - 15),
                                height: CGFloat(height)
                            )
                    }
                    .appendProcessor(DownsamplingImageProcessor(size: CGSize(width: width, height: height)))
                    .appendProcessor(RoundCornerImageProcessor(cornerRadius: 20))
                    .loadDiskFileSynchronously()
                    .cacheMemoryOnly()
                    .fade(duration: 0.25)
                    .frame(
                        width: CGFloat(width),
                        height: CGFloat(height)
                    )
                
                LinearGradient(
                    gradient: Gradient(colors: [.black, .clear, .clear]),
                    startPoint: .bottom,
                    endPoint: .top
                )
                .cornerRadius(20)
                .frame(width: CGFloat(width - 15), height: CGFloat(height))
                
                HStack {
                    Text(movie.originalTitle ?? "")
                        .font(.oswaldBold(size: 18))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(Color.white)
                        .padding(.horizontal, 16)
                        .padding(.bottom, 16)
                    
                    Spacer()
                }
                .frame(width: CGFloat(width))
            }
    }
}

struct MovieItemView_Previews: PreviewProvider {
    static var previews: some View {
        MovieItemView(movie: LocalMovie.exampleMovie)
    }
}
