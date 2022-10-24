//
//  TimelineInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import Foundation
import Combine

class TimelineInteractor: BaseInteractor {
    let localContentService: LocalContentService
    
    init(_ localContentService: LocalContentService) {
        self.localContentService = localContentService
    }
}

extension TimelineInteractor {
    
    func getTimelinePage(_ timelineId: Int) -> AnyPublisher<TimelinePage, Error> {
        return self.localContentService.getTimelinePage(timelineId: timelineId)
    }
}


