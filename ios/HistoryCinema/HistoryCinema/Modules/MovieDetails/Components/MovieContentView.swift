//
//  MovieContentView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/09/22.
//

import SwiftUI

struct MovieContentView: View {
    let movie: Movie!
    
    var body: some View {
        let backdropImageUrl = ImageUtils.formatImageUrl(imageID: movie.backdropPath, imageSize: TMDB.ImageSize.BACKDROP.w780) ?? ""
        let posterImageUrl = ImageUtils.formatImageUrl(imageID: movie.posterPath, imageSize: TMDB.ImageSize.POSTER.w780) ?? ""
        
        ZStack(alignment: .top) {
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
                    VStack(alignment: .leading) {
                        Text(movie.title ?? "")
                            .font(.oswaldBold(size: 22))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.white)
                        
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
                    
                    Spacer()
                    
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
                }
                .padding(.bottom, 10)
            }
            .frame(height: 350)
            
            HStack(alignment: .top) {
                VStack(alignment: .leading) {
                    if let directors = movie.directors(), !directors.isEmpty {
                        Text("Dirigido por")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(directors)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let writers = movie.writers(), !writers.isEmpty  {
                        Text("Escrito por")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(writers)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let originalLanguage = movie.language(), !originalLanguage.isEmpty {
                        Text("Idioma original")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(originalLanguage)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let runtime = movie.duration(), !runtime.isEmpty {
                        Text("Duração")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(runtime)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let releaseDate = movie.releaseDate?.toDate()?.formatDate(pattner: "dd/MM/yyyy"), !releaseDate.isEmpty {
                        Text("Lançamento")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(releaseDate)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    
                }
                .padding(.leading, 20)
                .padding(.vertical, 25)
                
                Spacer()
                
                VStack {
                    CustomImage(
                        imageUrl: posterImageUrl,
                        imageType: .online,
                        placeholderType: .movie,
                        width: 150,
                        height: 200,
                        cornerRadius: 20
                    )
                    .frame(width: 150, height: 200)
                }
                .padding(.vertical, 35)
                .padding(.trailing, 20)
            }
            .frame(maxWidth: .infinity)
            .background(Color.backgroundPrimary)
            .cornerRadius(12)
            .shadow(radius: 5)
            .padding(.horizontal, 10)
            .padding(.top, 340)
        }
    }
}

struct MovieContentView_Previews: PreviewProvider {
    static var previews: some View {
        ZStack(alignment: .topLeading) {
            ScrollView {
                LazyVStack {
                    MovieContentView(movie: Movie.exampleMovieFull())
                }
            }
            .edgesIgnoringSafeArea(.all)
        }
    }
}
