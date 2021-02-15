//
//  HomePresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import Combine

// MARK: HomePresenter

class HomePresenter {
    
    // MARK: Properties
    
    private var interactor: HomeInteractorInterface?
    private var wireframe: HomeWireframaInterface?
    
    @Published var homeContent: [HomeContentItem] = []
    
    private var movieService: MovieServiceProtocol
    private var cancalables: Set<AnyCancellable> = []
    
    init(movieService: MovieServiceProtocol) {
        self.movieService = movieService
    }
}

extension HomePresenter: ObservableObject {
    
    func fetchPopularMovies() {
        let localContentService = LocalContentService()
    
        localContentService.$homeContent
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
