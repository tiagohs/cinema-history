//
//  GlossaryPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 10/10/22.
//

import Foundation
import Combine
import SwiftUI

// MARK: GlossaryPresenter

class GlossaryPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: GlossaryInteractor?
    private var wireframe: GlossaryWireframe?
    
    @Published var glossaryList: [Glossary]
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: GlossaryInteractor, _ wireframe: GlossaryWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.glossaryList = []
    }
}

extension GlossaryPresenter {
    
    func fetchGlossary() {
        self.interactor?.getGlossary()
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.showErrorMessage = true
                    }
            }, receiveValue: { glossaryResult in
                self.glossaryList = glossaryResult.results
            })
            .store(in: &cancalables)
    }
}

