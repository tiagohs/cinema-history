//
//  ReferencesInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 06/10/22.
//

import Foundation
import Combine

class ReferencesInteractor: BaseInteractor {
    let localContentService: LocalContentService
    
    init(_ localContentService: LocalContentService) {
        self.localContentService = localContentService
    }
}

extension ReferencesInteractor {
    
    func getReferences() -> AnyPublisher<ReferencesResult, Error> {
        return self.localContentService.getReferences()
    }
}
