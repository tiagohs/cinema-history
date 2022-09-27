//
//  MilMoviesItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import SwiftUI
import Kingfisher

struct MilMoviesItem: View {
    
    let movie: Movie!
    
    var body: some View {
        let imageUrl = ImageUtils.formatImageUrl(imageID: movie.backdropPath, imageSize: TMDB.ImageSize.BACKDROP.w300)!
        let imageHeight = 200
        let genres = MovieUtils.getGenresName(ids: movie.genreIds)
        let releaseDate = movie.releaseDate?.toDate()
        
        HStack(alignment: .top) {
            VStack {
                Text(releaseDate?.formatDate(pattner: "dd MMM.") ?? "")
                    .font(.proximaNovaRegular(size: 16))
                    .textCase(.uppercase)
                    .foregroundColor(.textSecondary)
                Text(releaseDate?.year ?? "")
                    .font(.proximaNovaBold(size: 24))
                    .foregroundColor(.textPrimary)
            }
            .padding(.leading, 8)
            
            VStack {
                SmoothAsyncImageView(url: URL(string: imageUrl)) { phase in
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
                    .frame(height: CGFloat(imageHeight))
                    .cornerRadius(10)
                    .shadow(radius: 5)
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(Color(.sRGB, red: 150/255, green: 150/255, blue: 150/255, opacity: 0.1), lineWidth: 1)
                    )
                
                HStack(alignment: .top) {
                    VStack(alignment: .leading) {
                        Text(movie.originalTitle ?? "")
                            .font(.oswaldRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(movie.title ?? "")
                            .font(.oswaldBold(size: 22))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                        
                        if !genres.isEmpty {
                            Text(genres.joined(separator: ", "))
                                .font(.proximaNovaRegular(size: 12))
                                .multilineTextAlignment(.leading)
                                .foregroundColor(.textSecondary)
                        }
                    }
                    
                    Spacer()
                    
                    VStack {
                        Image(systemName: "info.circle")
                            .font(.system(size: 26, weight: .bold))
                            .foregroundColor(.textPrimary)
                        
                        Text("Saiba mais")
                            .font(.proximaNovaRegular(size: 14))
                            .foregroundColor(.textSecondary)
                            .padding(.top, 2)
                    }
                    .padding(.horizontal)
                }
                
                
                if let overview = movie.overview {
                    Text(overview)
                        .font(.proximaNovaRegular(size: 14))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.top, 3)
                        .padding(.trailing)
                }
                
            }
        }
        .padding(.vertical)
        .padding(.trailing, 3)
    }
}

struct MilMoviesItem_Previews: PreviewProvider {
    static var previews: some View {
        MilMoviesItem(movie: Movie.exampleMovie)
    }
}
