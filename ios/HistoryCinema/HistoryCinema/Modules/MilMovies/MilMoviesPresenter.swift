//
//  MilMoviesPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 26/09/22.
//

import Foundation

class MilMoviesPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: MilMoviesInteractor?
    private var wireframe: MilMoviesWireframe?
    
    @Published var movieList: [Movie]
    @Published var showErrorMessage: Bool = false
    @Published var isLoading: Bool = false
    
    private var isShowing = true
    private var page = 0
    private var totalPage = -1
    
    init(_ interactor: MilMoviesInteractor, _ wireframe: MilMoviesWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.movieList = []
        self.page = 0
        self.totalPage = -1
    }
    
    override func viewAppears() {
        self.isShowing = true
    }
    
    override func viewDisappears() {
        self.isShowing = false
    }
}

extension MilMoviesPresenter {
    
    func fetchMoviesBy(mainTopicItem: MilMoviesMainTopic) {
        if (page == totalPage || !isShowing) { return }
        
        self.isLoading = true
        self.page += 1
        self.interactor?.getMoviesBy(mainTopicItem.listId, page)
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.showErrorMessage = true
                    }
            }, receiveValue: { movieListResult in
                self.page = movieListResult.page ?? 1
                self.totalPage = movieListResult.total_pages ?? -1
                self.isLoading = false
                self.movieList += movieListResult.results ?? []
            })
            .store(in: &cancalables)
    }
}
