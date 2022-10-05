//
//  PersonDetailsWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import Foundation

class PersonDetailsWireframe {
    
}

// MARK: build's Module

extension PersonDetailsWireframe {
    
    static func buildPresenter() -> PersonDetailsPresenter {
        let personService = PersonService()
        let interactor = PersonDetailsInteractor(personService)
        let wireframe = PersonDetailsWireframe()
        
        return PersonDetailsPresenter(interactor, wireframe)
    }
}
