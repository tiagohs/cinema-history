//
//  PersonDetailsInteractor.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import Foundation
import Combine
import Alamofire

class PersonDetailsInteractor {
    let personService: PersonService
    
    init(_ personService: PersonService) {
        self.personService = personService
    }
}

extension PersonDetailsInteractor {
    
    func getPersonDetailsBy(_ id: Int) -> AnyPublisher<Person, AFError> {
        return self.personService.getDetails(
            personId: id,
            appendToResponse: [
                "tagged_images", "images", "movie_credits", "external_ids", "translations"
            ]
        )
    }
}

