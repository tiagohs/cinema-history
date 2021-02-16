//
//  HomeWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

class HomeWireframe: HomeWireframaInterface {
    
    
    
}

// MARK: build's Module

extension HomeWireframe {
    
    static func buildPresenter() -> HomePresenter {
        let movieService = MovieService()
        let interactor = HomeInteractor(movieService)
        let wireframe = HomeWireframe()
        
        return HomePresenter(interactor, wireframe)
    }
}
