//
//  MovieListSpecialView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/03/21.
//

import SwiftUI

struct MovieListItemSpecialView: View {
    let movie: LocalMovie
    
    var body: some View {
        let colorAsset = ColorUtils.getRandomColorAssets()
        let textColor = Color(UIColor(colorName: colorAsset.textColorName))
        let backgroundColor = Color(UIColor(colorName: "md_\(colorAsset.colorName)_500"))
        let backdropImageUrl = ImageUtils.formatImageUrl(imageID: movie.backdropPath, imageSize: TMDB.ImageSize.BACKDROP.w780) ?? ""
        let posterImageUrl = ImageUtils.formatImageUrl(imageID: movie.posterPath, imageSize: TMDB.ImageSize.POSTER.w780) ?? ""
        
        VStack {
            ZStack {
                ZStack(alignment: Alignment(horizontal: .leading, vertical: .top)) {
                    VStack(alignment: .leading, spacing: 0) {
                        CustomImage(
                            imageUrl: backdropImageUrl,
                            imageType: .online,
                            placeholderType: .movie,
                            height: 200,
                            blurRadius: 2
                        )
                            .frame(height: 200)
                        
                        DividerColorView(colorName: colorAsset.colorName)
                        
                        Text(movie.title ?? movie.originalTitle ?? "")
                            .font(.oswaldBold(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(textColor)
                            .padding(.leading, 182)
                        
                        Text(movie.overview ?? "")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(textColor)
                            .padding(.top, 80)
                            .padding(.horizontal, 32)
                            .padding(.bottom, 16)
                    }
                    .frame(width: UIScreen.main.bounds.width)
                    
                    CustomImage(
                        imageUrl: posterImageUrl,
                        imageType: .online,
                        placeholderType: .movie,
                        width: 150,
                        height: 200,
                        cornerRadius: 20
                    )
                    .padding(.leading, 50)
                    .padding(.top, 200)
                    .frame(width: 150, height: 200)
                }
                .background(backgroundColor)
            }
            .frame(
                width: UIScreen.main.bounds.width - 32)
            .background(Color.white)
            .cornerRadius(10)
        }
        .background(Color.white)
    }
}

struct MovieListItemSpecialView_Previews: PreviewProvider {
    static var previews: some View {
        MovieListItemSpecialView(movie: LocalMovie.exampleMovie)
    }
}
