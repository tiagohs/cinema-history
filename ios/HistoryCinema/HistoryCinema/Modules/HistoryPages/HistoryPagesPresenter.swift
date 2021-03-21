//
//  HistoryPagesPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import Foundation
import Combine

// MARK: SummaryPresenter

class HistoryPagesPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: HistoryPagesInteractor?
    private var wireframe: HistoryPagesWireframe?
    
    @Published var summaryList: [SummaryModel]
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: HistoryPagesInteractor, _ wireframe: HistoryPagesWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.summaryList = []
    }
}

extension HistoryPagesPresenter {
    
//    func fetchSummaryBy(mainTopicItem: MainTopicItem) {
//        self.interactor?.getSummaryBy(mainTopicItem.id)
//            .receive(on: RunLoop.main)
//            .sink(receiveCompletion: { completion in
//                switch completion {
//                    case .finished: print("🏁 finished")
//                    case .failure(let error):
//                        print(error)
//                        self.showErrorMessage = true
//                    }
//            }, receiveValue: { summaryListResult in
//                self.summaryList = summaryListResult.results
//            })
//            .store(in: &cancalables)
//    }
}

