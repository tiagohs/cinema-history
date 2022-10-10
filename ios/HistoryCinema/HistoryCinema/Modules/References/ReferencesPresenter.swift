//
//  ReferencesPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 06/10/22.
//

import Foundation
import Combine
import SwiftUI

// MARK: ReferencesPresenter

class ReferencesPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: ReferencesInteractor?
    private var wireframe: ReferencesWireframe?
    
    @Published var references: [ReferenceContent]
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: ReferencesInteractor, _ wireframe: ReferencesWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.references = []
    }
}

extension ReferencesPresenter {
    
    func fetchReferences() {
        self.interactor?.getReferences()
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.showErrorMessage = true
                    }
            }, receiveValue: { referencesResult in
                self.references = referencesResult.results
            })
            .store(in: &cancalables)
    }
}
