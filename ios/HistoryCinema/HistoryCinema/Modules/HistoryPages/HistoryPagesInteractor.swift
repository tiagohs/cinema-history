//
//  HistoryPagesInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import Foundation
import Combine
import Alamofire

class HistoryPagesInteractor: BaseInteractor {
    let localContentService: LocalContentService
    
    init(_ localContentService: LocalContentService) {
        self.localContentService = localContentService
    }
}

extension HistoryPagesInteractor {
    
    func getPage(_ mainTopicId: Int, _ sumarioID: Int) -> AnyPublisher<Page, Error> {
        return self.localContentService.getPage(mainTopicId: mainTopicId, sumarioID: sumarioID)
    }
}

