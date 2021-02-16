//
//  HomeInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

// MARK: HomeInteractor: BaseInteractor

class HomeInteractor: BaseInteractor {
    let movieService: MovieServiceProtocol
    
    init(_ movieService: MovieServiceProtocol) {
        self.movieService = movieService
    }
    
}

extension HomeInteractor: HomeInteractorInterface {
    
}
