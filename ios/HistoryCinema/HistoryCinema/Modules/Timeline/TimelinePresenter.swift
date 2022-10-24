//
//  TimelinePresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import Foundation

import Foundation
import Combine
import SwiftUI

// MARK: GlossaryPresenter

class TimelinePresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: TimelineInteractor?
    private var wireframe: TimelineWireframe?
    
    @Published var timelinePage: TimelinePage?
    @Published var pageTitle: String?
    @Published var showErrorMessage: Bool = false
    @Published var isLoading: Bool = false
    
    init(_ interactor: TimelineInteractor, _ wireframe: TimelineWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
    }
}

extension TimelinePresenter {
    
    func fetchTimelinePage(_ timelineId: Int) {
        self.isLoading = true
        self.interactor?.getTimelinePage(timelineId)
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.isLoading = false
                        self.showErrorMessage = true
                    }
            }, receiveValue: { timelinePage in
                self.isLoading = false
                self.timelinePage = timelinePage
                
                if let firstTimelineTitle = timelinePage.timelineList?.first as? TimelineTitle {
                    self.pageTitle = firstTimelineTitle.pageTitle
                }
            })
            .store(in: &cancalables)
    }
}

