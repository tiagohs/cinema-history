//
//  GlossaryInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 10/10/22.
//

import Foundation
import Combine

class GlossaryInteractor: BaseInteractor {
    let localContentService: LocalContentService
    
    init(_ localContentService: LocalContentService) {
        self.localContentService = localContentService
    }
}

extension GlossaryInteractor {
    
    func getGlossary() -> AnyPublisher<GlossaryResult, Error> {
        return self.localContentService.getGlossary()
    }
}

