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

// MARK: HomePresenter: BasePresenter

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

// MARK: HomePresenter: Fetchs

extension HomePresenter {
    
    func fetchHomeContent() {
        self.interactor?.getHomeContent()
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("")
                    case .failure:
                        self.showErrorMessage = true
                    }
            }, receiveValue: { homeContentResult in
                self.homeContent = homeContentResult.results
            })
            .store(in: &cancalables)
    }
}

// MARK: HomePresenter: Present Views

extension HomePresenter {
    
    @ViewBuilder
    func presentMainTopics(_ mainTopicType: MainTopicsType) -> some View {
        self.wireframe?.presentMainTopics(mainTopicType)
    }
    
    @ViewBuilder
    func presentTimeline() -> some View {
        self.wireframe?.presentTimeline()
    }
    
    
    @ViewBuilder
    func presentReferences() -> some View {
        self.wireframe?.presentReferences()
    }
    
    @ViewBuilder
    func presentGlossary() -> some View {
        self.wireframe?.presentGlossary()
    }
    
    @ViewBuilder
    func presentAbout() -> some View {
        self.wireframe?.presentAbout()
    }
}

