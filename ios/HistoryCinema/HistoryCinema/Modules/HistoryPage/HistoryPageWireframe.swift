//
//  HistoryWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/03/21.
//

import Foundation

class HistoryPageWireframe {
    
    
}

// MARK: build's Module

extension HistoryPageWireframe {
    
    static func buildPresenter() -> HistoryPagePresenter {
        let localContentService = LocalContentService()
        let interactor = HistoryPageInteractor(localContentService)
        let wireframe = HistoryPageWireframe()
        
        return HistoryPagePresenter(interactor, wireframe)
    }
}
