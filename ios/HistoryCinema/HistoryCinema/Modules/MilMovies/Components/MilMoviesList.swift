//
//  MilMoviesList.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import SwiftUI

struct MilMoviesList<Content : View>: View {
    
    let movieList: [Movie]
    let onLastItemAppear: () -> Void
    let onItemClicked: (Movie) -> Void
    
    @ViewBuilder var MovieDetailsDestination: (_ movie: Movie) -> Content
    
    var body: some View {
        ForEach(self.movieList, id: \.self) { movie in
            MilMoviesItem(movie: movie)
                .onTapGesture {
                    onItemClicked(movie)
                }
                .onAppear {
                    if movie.id == self.movieList.last?.id {
                        onLastItemAppear()
                    }
               }
        }
    }
}

struct MilMoviesYears_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            MilMoviesList(movieList: ResultsList.exampleMovieResultsList.results ?? []) {
                
            } onItemClicked: { movie in
                
            } MovieDetailsDestination: { movie in
                AnyView(EmptyView())
            }
        }
    }
}
