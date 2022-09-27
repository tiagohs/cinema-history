//
//  MilMoviesInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 26/09/22.
//

import Foundation
import Combine
import Alamofire

class MilMoviesInteractor {
    let movieService: MovieService
    
    init(_ movieService: MovieService) {
        self.movieService = movieService
    }
}

extension MilMoviesInteractor {
    
    func getMoviesBy(_ movieListId: String, _ page: Int) -> AnyPublisher<ResultsList, AFError> {
        return self.movieService.getList(listId: movieListId, page: page)
    }
}
