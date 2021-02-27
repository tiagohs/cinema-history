//
//  HomeInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import Combine
import Alamofire

// MARK: HomeInteractor: BaseInteractor

class HomeInteractor: BaseInteractor {
    let localContentService: LocalContentService
    
    init(_ localContentService: LocalContentService) {
        self.localContentService = localContentService
    }
    
}

extension HomeInteractor {
    
    func getHomeContent() -> AnyPublisher<HomeContentResult, AFError> {
        return self.localContentService.getHomeContent()
    }
}
