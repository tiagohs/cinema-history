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
        return Deferred {
                Future { promise in
                    guard let file = "homecontent.json".toFileURL() else {
                        promise(.failure(AFError.createURLRequestFailed(error: "Could't find homecontent.json in main bundle")))
                        return
                    }
                    
                    AF.request(file, method: .get)
                                .validate()
                                .responseObject { (response: AFDataResponse<HomeContentResult>) in
                                    promise(response.result)
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

