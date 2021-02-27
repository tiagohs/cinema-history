//
//  HomeWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

class HomeWireframe {
    
    
}

// MARK: build's Module

extension HomeWireframe {
    
    static func buildPresenter() -> HomePresenter {
        let localContentService = LocalContentService()
        let interactor = HomeInteractor(localContentService)
        let wireframe = HomeWireframe()
        
        return HomePresenter(interactor, wireframe)
    }
}
