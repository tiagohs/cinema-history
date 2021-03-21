//
//  HistoryPagesWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import Foundation

class HistoryPagesWireframe {
    
    
}

// MARK: build's Module

extension HistoryPagesWireframe {
    
    static func buildPresenter() -> HistoryPagesPresenter {
        let localContentService = LocalContentService()
        let interactor = HistoryPagesInteractor(localContentService)
        let wireframe = HistoryPagesWireframe()
        
        return HistoryPagesPresenter(interactor, wireframe)
    }
}
