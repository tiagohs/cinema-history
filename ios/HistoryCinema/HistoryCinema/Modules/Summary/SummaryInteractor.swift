//
//  SummaryInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import Foundation
import Combine
import Alamofire

class SummaryInteractor: BaseInteractor {
    let localContentService: LocalContentService
    
    init(_ localContentService: LocalContentService) {
        self.localContentService = localContentService
    }
}

extension SummaryInteractor {
    
    func getSummaryBy(_ mainTopicID: Int) -> AnyPublisher<SummaryResult, AFError> {
        return self.localContentService.getSummaryBy(mainTopicID: mainTopicID)
    }
}
