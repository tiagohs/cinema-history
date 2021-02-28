//
//  HomeInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import Combine

class MainTopicsInteractor: BaseInteractor {
    let localContentService: LocalContentService
    
    init(_ localContentService: LocalContentService) {
        self.localContentService = localContentService
    }
}

extension MainTopicsInteractor {
    
    func getMainTopicsBy(mainTopicType: MainTopicsType) -> AnyPublisher<MainTopicsResult, Error> {
        return self.localContentService.getMainTopicsBy(mainTopicType: mainTopicType)
    }
}
