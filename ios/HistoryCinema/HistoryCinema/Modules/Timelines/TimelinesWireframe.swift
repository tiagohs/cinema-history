//
//  TimelinesWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import Foundation

class TimelinesWireframe {
    
}

// MARK: build's Module

extension TimelinesWireframe {
    
    static func buildPresenter() -> TimelinesPresenter {
        let interactor = TimelinesInteractor()
        let wireframe = TimelinesWireframe()
        
        return TimelinesPresenter(interactor, wireframe)
    }
}
