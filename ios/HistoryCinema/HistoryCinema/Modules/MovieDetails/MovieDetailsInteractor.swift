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
    let localContentService: LocalContentService
    
    init(_ movieService: MovieService, _ localContentService: LocalContentService) {
        self.movieService = movieService
        self.localContentService = localContentService
    }
}

extension MovieDetailsInteractor {
    
    
    
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
    
    func getMovieRankingBy(_ imdbId: String) -> AnyPublisher<MovieOMDB, AFError> {
        return self.movieService.getMovieRankings(imdbId: imdbId)
    }
    
    func getSpecialMovies() -> AnyPublisher<ExtraInfoResult, AFError> {
        return self.localContentService.getSpecialMovies()
    }
}
