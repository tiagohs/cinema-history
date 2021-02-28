//
//  SummaryWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import Foundation

class SummaryWireframe {
    
    
}

// MARK: build's Module

extension SummaryWireframe {
    
    static func buildPresenter() -> SummaryPresenter {
        let localContentService = LocalContentService()
        let interactor = SummaryInteractor(localContentService)
        let wireframe = SummaryWireframe()
        
        return SummaryPresenter(interactor, wireframe)
    }
}
