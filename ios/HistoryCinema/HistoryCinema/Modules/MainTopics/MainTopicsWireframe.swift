//
//  MainTopicsWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation

class MainTopicsWireframe {
    
    
}

// MARK: build's Module

extension MainTopicsWireframe {
    
    static func buildPresenter() -> MainTopicsPresenter {
        let localContentService = LocalContentService()
        let interactor = MainTopicsInteractor(localContentService)
        let wireframe = MainTopicsWireframe()
        
        return MainTopicsPresenter(interactor, wireframe)
    }
}
