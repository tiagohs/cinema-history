//
//  ReferencesWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 06/10/22.
//

import Foundation

class ReferencesWireframe {
    
}

// MARK: build's Module

extension ReferencesWireframe {
    
    static func buildPresenter() -> ReferencesPresenter {
        let localContentService = LocalContentService()
        let interactor = ReferencesInteractor(localContentService)
        let wireframe = ReferencesWireframe()
        
        return ReferencesPresenter(interactor, wireframe)
    }
}
