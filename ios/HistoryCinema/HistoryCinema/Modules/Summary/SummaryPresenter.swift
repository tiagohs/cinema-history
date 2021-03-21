//
//  SummaryPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import Foundation
import Combine

// MARK: SummaryPresenter

class SummaryPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: SummaryInteractor?
    private var wireframe: SummaryWireframe?
    
    @Published var summaryList: [SummaryModel]
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: SummaryInteractor, _ wireframe: SummaryWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.summaryList = []
    }
}

extension SummaryPresenter {
    
    func fetchSummaryBy(mainTopicItem: MainTopicItem) {
        self.interactor?.getSummaryBy(mainTopicItem.id)
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.showErrorMessage = true
                    }
            }, receiveValue: { summaryListResult in
                self.summaryList = summaryListResult.results
            })
            .store(in: &cancalables)
    }
}
