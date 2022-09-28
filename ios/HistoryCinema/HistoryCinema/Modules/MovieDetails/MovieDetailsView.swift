//
//  MovieDetailsView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import SwiftUI

struct MovieDetailsView: View {
    
    let movieId: Int!
    
    @ObservedObject private var presenter: MovieDetailsPresenter = MovieDetailsWireframe.buildPresenter()
    
    var body: some View {
        VStack {
            Text(presenter.movie?.title ?? "")
        }
        .onAppear {
            presenter.viewAppears()
            presenter.fetchMovieDetailsBy(movieId)
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct MovieDetailsView_Previews: PreviewProvider {
    static var previews: some View {
        MovieDetailsView(movieId: 775)
    }
}
