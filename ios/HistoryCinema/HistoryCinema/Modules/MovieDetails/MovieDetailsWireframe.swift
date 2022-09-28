//
//  MovieDetailsWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import Foundation
import SwiftUI

class MovieDetailsWireframe {
    
}

// MARK: build's Module

extension MovieDetailsWireframe {
    
    static func buildPresenter() -> MovieDetailsPresenter {
        let movieService = MovieService()
        let interactor = MovieDetailsInteractor(movieService)
        let wireframe = MovieDetailsWireframe()
        
        return MovieDetailsPresenter(interactor, wireframe)
    }
}
