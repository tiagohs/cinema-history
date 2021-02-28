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
    
    @Published var mainTopics: [MainTopic]
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: MainTopicsInteractor, _ wireframe: MainTopicsWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.mainTopics = []
    }
}

extension MainTopicsPresenter {
    
    func fetchMainTopicsBy(_ mainTopicType: MainTopicsType) {
        self.interactor?.getMainTopicsBy(mainTopicType: mainTopicType)
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.showErrorMessage = true
                    }
            }, receiveValue: { mainTopicsResult in
                self.mainTopics = mainTopicsResult.results
            })
            .store(in: &cancalables)
    }
}
