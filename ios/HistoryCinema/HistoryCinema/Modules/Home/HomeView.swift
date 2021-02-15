//
//  HomeView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import SwiftUI
import Combine

struct HomeView: View, HomeViewInterface {
    
    @EnvironmentObject var presenter: HomePresenter
    
    var body: some View {
        VStack {
            ForEach(presenter.movies, id: \.self) { (movie) in
                Text("\(movie.title!)")
            }
        }
        .onAppear { presenter.fetchPopularMovies() }
    }
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        let movieService = MovieService()
        let presenter = HomePresenter(movieService: movieService)
        
        HomeView()
            .environmentObject(presenter)
    }
}
