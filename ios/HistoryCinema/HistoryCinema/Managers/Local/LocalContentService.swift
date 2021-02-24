//
//  LocalContentService.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import Combine
import Alamofire

class LocalContentService: BaseLocalService {
    
    func getHomeContent() -> AnyPublisher<HomeContentResult, AFError> {
        return getLocalFile("homecontent.json") as AnyPublisher<HomeContentResult, AFError>
    }
    
    func getSummaryBy(mainTopicID: Int) -> AnyPublisher<SummaryResult, AFError> {
        return getLocalFile("hmt_sumarios_\(mainTopicID).json") as AnyPublisher<SummaryResult, AFError>
    }
    
    func getAwardsNomineesBy(awardId: Int) -> AnyPublisher<NomineeResult, Error> {
        return Deferred {
                Future { promise in
                    do {
                        let values = try self.loadNominees("nominees_\(awardId).json")

                        promise(.success(values))
                    } catch {
                        promise(.failure(error))
                    }
                }
            }.eraseToAnyPublisher()
    }
    
    func getAwardsHistoryBy(awardId: Int) -> AnyPublisher<HistoryResult, Error> {
        return Deferred {
                Future { promise in
                    do {
                        let values = try self.loadAwardsHistory("history\(awardId).json")

                        promise(.success(values))
                    } catch {
                        promise(.failure(error))
                    }
                }
            }.eraseToAnyPublisher()
    }
    
    func getGlossary() -> AnyPublisher<GlossaryResult, Error> {
        return Deferred {
                Future { promise in
                    do {
                        let values = try self.loadGlossary("glossary.json")

                        promise(.success(values))
                    } catch {
                        promise(.failure(error))
                    }
                }
            }.eraseToAnyPublisher()
    }
    
    func getReferences() -> AnyPublisher<ReferencesResult, Error> {
        return Deferred {
                Future { promise in
                    do {
                        let values = try self.loadReferencesContent("references.json")

                        promise(.success(values))
                    } catch {
                        promise(.failure(error))
                    }
                }
            }.eraseToAnyPublisher()
    }
    
    func getPage(mainTopicId: Int, sumarioID: Int) -> AnyPublisher<Page, Error> {
        return Deferred {
                Future { promise in
                    do {
                        let values = try self.loadPages("main_\(mainTopicId)_page_\(sumarioID).json")

                        promise(.success(values))
                    } catch {
                        promise(.failure(error))
                    }
                }
            }.eraseToAnyPublisher()
    }
}

