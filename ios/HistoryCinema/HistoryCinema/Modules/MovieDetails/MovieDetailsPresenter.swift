//
//  MovieDetailsPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import Foundation
import Combine

class MovieDetailsPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: MovieDetailsInteractor?
    private var wireframe: MovieDetailsWireframe?
    
    @Published var movie: Movie? = nil
    @Published var showErrorMessage: Bool = false
    @Published var isLoading: Bool = false
    
    init(_ interactor: MovieDetailsInteractor, _ wireframe: MovieDetailsWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
    }
    
    override func viewAppears() {
        
    }
}

extension MovieDetailsPresenter {
    
    func fetchMovieDetailsBy(_ movieId: Int) {
        self.isLoading = true
        
        self.interactor?.getMovieDetailsBy(movieId)
            .receive(on: RunLoop.main)
            .sink(receiveCompletion: { completion in
                switch completion {
                    case .finished: print("🏁 finished")
                    case .failure(let error):
                        print(error)
                        self.showErrorMessage = true
                    }
            }, receiveValue: { movie in
                self.isLoading = false
                self.movie = movie
            })
            .store(in: &cancalables)
    }
    
    private func fetchAppendInfos() {
        
    }
}
