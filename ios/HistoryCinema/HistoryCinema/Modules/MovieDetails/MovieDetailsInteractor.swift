//
//  MovieDetailsInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import Foundation
import Combine
import Alamofire

class MovieDetailsInteractor {
    let movieService: MovieService
    
    init(_ movieService: MovieService) {
        self.movieService = movieService
    }
}

extension MovieDetailsInteractor {
    
    func getMovieRankingBy(_ imdbId: String) -> AnyPublisher<MovieOMDB, AFError> {
        return self.movieService.getMovieRankings(imdbId: imdbId)
    }
    
    func getMovieDetailsBy(_ id: Int) -> AnyPublisher<Movie, AFError> {
        return self.movieService.getDetails(
            movieId: id,
            appendToResponse: [
                "credits",
                "images",
                "videos",
                "keywords",
                "releases",
                "similar_movies",
                "external_ids",
                "translations"
            ]
        )
    }
}
