//
//  PersonDetailsPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import Foundation
import Combine

class PersonDetailsPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: PersonDetailsInteractor?
    private var wireframe: PersonDetailsWireframe?
    
    @Published var person: Person? = nil
    @Published var showErrorMessage: Bool = false
    @Published var isLoading: Bool = true
    
    init(_ interactor: PersonDetailsInteractor, _ wireframe: PersonDetailsWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
    }
    
    override func viewAppears() {
        
    }
}

extension PersonDetailsPresenter {
    
    func fetchPersonDetailsBy(_ personId: Int) {
        self.interactor?.getPersonDetailsBy(personId)
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.showErrorMessage = true
                    }
            }, receiveValue: { person in
                self.person = person
                self.isLoading = false
            })
            .store(in: &cancalables)
    }
}
