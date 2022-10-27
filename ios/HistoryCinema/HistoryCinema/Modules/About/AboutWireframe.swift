//
//  AboutWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/10/22.
//

import Foundation

class AboutWireframe {
    
}

// MARK: build's Module

extension AboutWireframe {
    
    static func buildPresenter() -> AboutPresenter {
        let interactor = AboutInteractor()
        let wireframe = AboutWireframe()
        
        return AboutPresenter(interactor, wireframe)
    }
}
