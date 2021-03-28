//
//  HistoryPagesPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import Foundation
import Combine

// MARK: SummaryPresenter

class HistoryPagePresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: HistoryPageInteractor?
    private var wireframe: HistoryPageWireframe?
    
    @Published var historyPage: Page?
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: HistoryPageInteractor, _ wireframe: HistoryPageWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
    }
}

extension HistoryPagePresenter {
    
    func fetchPageBy(_ mainTopic: MainTopicItem, _ summary: SummaryModel) {
        self.interactor?.getPage(mainTopic.id, summary.id)
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.showErrorMessage = true
                    }
            }, receiveValue: { page in
                self.historyPage = page
            })
            .store(in: &cancalables)
    }
}

