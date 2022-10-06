//
//  MovieListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import SwiftUI

import struct Kingfisher.DownsamplingImageProcessor

struct MovieListView: View {
    let movieList: [MovieDTO]
    var posterWidth: Int? = nil
    var posterHeight: Int? = nil
    
    var onClickListLink: ((TextViewLinkScreenType, Int?) -> Void)? = nil
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            LazyHStack(alignment: .top, spacing: 0) {
                ForEach(0 ..< movieList.count) { index in
                    let movie = movieList[index]
                    
                    MovieItemView(movie: movie, posterWidth: posterWidth, posterHeight: posterHeight)
                        .padding(.leading, index == 0 ? 16 : 8)
                        .onTapGesture {
                            onClickListLink?(TextViewLinkScreenType.movie, movie.id)
                        }
                }
            }
        }
    }
}

struct MovieListView_Previews: PreviewProvider {
    static var previews: some View {
        let movieList = [
            MovieDTO.fromLocalMovie(LocalMovie.exampleMovie),
            MovieDTO.fromLocalMovie(LocalMovie.exampleMovie),
            MovieDTO.fromLocalMovie(LocalMovie.exampleMovie),
            MovieDTO.fromLocalMovie(LocalMovie.exampleMovie)
        ]
        
        MovieListView(movieList: movieList)
    }
}
