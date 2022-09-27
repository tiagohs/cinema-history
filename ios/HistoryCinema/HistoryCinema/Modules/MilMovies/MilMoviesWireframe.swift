//
//  MilMoviesWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 26/09/22.
//

import Foundation
import SwiftUI

class MilMoviesWireframe {
    
}

// MARK: build's Module

extension MilMoviesWireframe {
    
    static func buildPresenter() -> MilMoviesPresenter {
        let movieService = MovieService()
        let interactor = MilMoviesInteractor(movieService)
        let wireframe = MilMoviesWireframe()
        
        return MilMoviesPresenter(interactor, wireframe)
    }
}
