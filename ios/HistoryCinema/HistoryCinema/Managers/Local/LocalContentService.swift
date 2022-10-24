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
    
    func getSpecialPersons() -> AnyPublisher<PersonExtraInfo, AFError> {
        return getLocalFile("persons.json") as AnyPublisher<PersonExtraInfo, AFError>
    }
    
    func getSpecialMovies() -> AnyPublisher<ExtraInfoResult, AFError> {
        return getLocalFile("movies.json") as AnyPublisher<ExtraInfoResult, AFError>
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
    
    func getMainTopicsBy(mainTopicType: MainTopicsType) -> AnyPublisher<MainTopicsResult, Error> {
        switch mainTopicType {
        case .awards:
            return self.loadMainTopics("awards.json")
        case .directors:
            return self.loadMainTopics("directorsmaintopics.json")
        case .history_cinema:
            return self.loadMainTopics("maintopics.json")
        case .mil_movies:
            return self.loadMainTopics("milmoviesmaintopics.json")
        default:
            return self.loadMainTopics("maintopics.json")
        }
    }
    
    func getTimelinePage(timelineId: Int) -> AnyPublisher<TimelinePage, Error> {
        return Deferred {
                Future { promise in
                    do {
                        guard let timelinePageJSON = try "timeline_\(timelineId).json".toJSONObject() as? Dictionary<String, Any> else {
                            throw "Could't create JSONObject"
                        }
                        
                        guard let timelineList = timelinePageJSON["timeline_list"] as? Array<Dictionary<String, Any>> else {
                            throw "Could't find results timeline_list key"
                        }
                        
                        guard let timelinePage = TimelinePage(JSON: timelinePageJSON) else {
                            throw "Could't create TimelinePage"
                        }
                        
                        timelinePage.timelineList = try self.loadTimeline(timelineList)
                        
                        promise(.success(timelinePage))
                    } catch {
                        promise(.failure(error))
                    }
                }
            }.eraseToAnyPublisher()
    }
}
