//
//  HistoryInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/03/21.
//

import Combine
import Alamofire

class HistoryPageInteractor: BaseInteractor {
    let localContentService: LocalContentService
    
    init(_ localContentService: LocalContentService) {
        self.localContentService = localContentService
    }
}

extension HistoryPageInteractor {
    
    func getPage(_ mainTopicId: Int, _ sumarioID: Int) -> AnyPublisher<Page, Error> {
        return self.localContentService.getPage(mainTopicId: mainTopicId, sumarioID: sumarioID)
    }
}

