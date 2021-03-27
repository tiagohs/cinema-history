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
        let imageUrl = ImageUtils.formatImageUrl(imageID: movie.posterPath, imageSize: TMDB.ImageSize.POSTER.w780) ?? ""
            
        ItemImageView(
            url: imageUrl,
            title: movie.originalTitle,
            imageWidth: posterWidth,
            imageHeight: posterHeight)
    }
}

struct MovieItemView_Previews: PreviewProvider {
    static var previews: some View {
        MovieItemView(movie: LocalMovie.exampleMovie)
    }
}
