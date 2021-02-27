//
//  MainTopicsPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import Combine

// MARK: MainTopicsPresenter

class MainTopicsPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: MainTopicsInteractor?
    private var wireframe: MainTopicsWireframe?
    
    init(_ interactor: MainTopicsInteractor, _ wireframe: MainTopicsWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
    }
    
}
