//
//  PersonServiceInterface.swift
//  popmovies
//
//  Created by Tiago Silva on 06/07/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Combine
import Alamofire

// MARK: PersonServiceInterface

protocol PersonServiceProtocol {
    
    func getDetails(personId: Int, appendToResponse: [String], language: String) -> AnyPublisher<Person, AFError>
    func getPersonList(url: String, parameters: [String : String]) -> AnyPublisher<ResultsPerson, AFError>
    func getImages(personId: Int) -> AnyPublisher<ImageResults, AFError>
    func getTaggedImages(_ personId: Int, language: String) -> AnyPublisher<TaggedImages, AFError>
    func getTranslations(_ personId: Int) -> AnyPublisher<TranslationResults, AFError>
    func getAllImages(personId: Int, language: String) -> AnyPublisher<ImageResultDTO, AFError>
}
