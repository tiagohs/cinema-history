//
//  GenreServiceInterface.swift
//  popmovies
//
//  Created by Tiago Silva on 06/07/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Combine
import Alamofire

// MARK: GenreServiceInterface

protocol GenreServiceProtocol {
    
    func getGenres(language: String) -> AnyPublisher<[Genre], AFError>
    func getMoviesByGenre(genreId: Int, page: Int, language: String) -> AnyPublisher<ResultsMovie, AFError>
}
