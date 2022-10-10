//
//  GlossaryWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 10/10/22.
//

import Foundation

class GlossaryWireframe {
    
}

// MARK: build's Module

extension GlossaryWireframe {
    
    static func buildPresenter() -> GlossaryPresenter {
        let localContentService = LocalContentService()
        let interactor = GlossaryInteractor(localContentService)
        let wireframe = GlossaryWireframe()
        
        return GlossaryPresenter(interactor, wireframe)
    }
}
