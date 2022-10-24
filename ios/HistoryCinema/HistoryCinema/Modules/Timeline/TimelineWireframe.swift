//
//  TimelineWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import Foundation

class TimelineWireframe {
    
}

// MARK: build's Module

extension TimelineWireframe {
    
    static func buildPresenter() -> TimelinePresenter {
        let localContentService = LocalContentService()
        let interactor = TimelineInteractor(localContentService)
        let wireframe = TimelineWireframe()
        
        return TimelinePresenter(interactor, wireframe)
    }
}
