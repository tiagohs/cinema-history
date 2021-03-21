//
//  MovieListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import SwiftUI

import struct Kingfisher.DownsamplingImageProcessor

struct MovieListView: View {
    let movieList: [LocalMovie]
    var posterWidth: Int? = nil
    var posterHeight: Int? = nil
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .top, spacing: 0) {
                ForEach(0 ..< movieList.count) { index in
                    let movie = movieList[index]
                    
                    MovieItemView(movie: movie, posterWidth: posterWidth, posterHeight: posterHeight)
                        .padding(.leading, 8)
                }
            }
        }
    }
}

struct MovieListView_Previews: PreviewProvider {
    static var previews: some View {
        let movieList = [
            LocalMovie.exampleMovie,
            LocalMovie.exampleMovie,
            LocalMovie.exampleMovie,
            LocalMovie.exampleMovie
        ]
        
        MovieListView(movieList: movieList)
    }
}
