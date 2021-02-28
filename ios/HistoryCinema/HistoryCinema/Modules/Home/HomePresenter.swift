//
//  HomePresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import Combine
import Alamofire
import ObjectMapper
import SwiftUI

// MARK: HomePresenter

class HomePresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: HomeInteractor?
    private var wireframe: HomeWireframe?
    
    @Published var homeContent: [HomeContentItem]
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: HomeInteractor, _ wireframe: HomeWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.homeContent = []
    }
    
    override func viewAppears() {
        fetchHomeContent()
    }
}

extension HomePresenter {
    
    func fetchHomeContent() {
        self.interactor?.getHomeContent()
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure:
                        self.showErrorMessage = true
                    }
            }, receiveValue: { homeContentResult in
                self.homeContent = homeContentResult.results
            })
            .store(in: &cancalables)
    }
}

extension HomePresenter {
    
    @ViewBuilder
    func presentMainTopics(_ mainTopicType: MainTopicsType) -> some View {
        self.wireframe?.presentMainTopics(mainTopicType)
    }
}
