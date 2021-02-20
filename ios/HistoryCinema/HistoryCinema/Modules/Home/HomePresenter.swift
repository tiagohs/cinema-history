//
//  HomePresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import Combine

// MARK: HomePresenter

class HomePresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: HomeInteractorInterface?
    private var wireframe: HomeWireframaInterface?
    
    @Published var homeContent: [HomeContentItem]
    
    init(_ interactor: HomeInteractorInterface, _ wireframe: HomeWireframaInterface) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.homeContent = []
    }
    
    override func viewAppears() {
        fetchPopularMovies()
    }
}

extension HomePresenter {
    
    func fetchPopularMovies() {
        let localContentService = LocalContentService()

        localContentService.getHomeContent()
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error): print("❗️ failure: \(error)")
                    }
            }, receiveValue: { values in
                self.homeContent = values
                print(values)
            })
            .store(in: &cancalables)
    }
}
