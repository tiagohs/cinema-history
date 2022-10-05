//
//  PersonService.swift
//  popmovies
//
//  Created by Tiago Silva on 19/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation
import Combine
import Alamofire

// MARK: PersonService: PersonServiceInterface

class PersonService: PersonServiceProtocol {
    
    func getDetails(personId: Int, appendToResponse: [String], language: String = Locale.getCurrentAppLangAndRegion()) -> AnyPublisher<Person, AFError> {
        let url = TMDB.URL.PERSON.buidPersonDetailsUrl(personId: personId)
        let parameters = TMDB.URL.PERSON.buildPersonDetailsParameters(appendToResponse, language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: Person.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getPersonList(url: String, parameters: [String : String]) -> AnyPublisher<ResultsPerson, AFError> {
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: ResultsPerson.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getImages(personId: Int) -> AnyPublisher<ImageResults, AFError> {
        let url = TMDB.URL.PERSON.buildImagesUrl(personId: personId)
        let parameters = TMDB.URL.PERSON.buildImagesParameters()
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: ImageResults.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getTaggedImages(_ personId: Int, language: String) -> AnyPublisher<TaggedImages, AFError> {
        let url = TMDB.URL.PERSON.buildTaggedImagesUrl(personId: personId)
        let parameters = TMDB.URL.PERSON.buildTaggedImagesParameters(language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: TaggedImages.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getTranslations(_ personId: Int) -> AnyPublisher<TranslationResults, AFError>  {
        let url = TMDB.URL.PERSON.buildTranslationsPersonUrl(personId: personId)
        let parameters = TMDB.URL.MOVIES.buildTranslationsParameters()
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: TranslationResults.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getAllImages(personId: Int, language: String) -> AnyPublisher<ImageResultDTO, AFError> {
        var imageResultDTO = ImageResultDTO(images: [], translations: [])
        
        return Publishers.CombineLatest(getImages(personId: personId), getTaggedImages(personId, language: language))
                    .map { (imageResults, taggedImages) in
                        imageResultDTO.images = Array(Set(imageResults.profiles ?? [] + self.mergeImages(taggedImages)))
                        
                        return imageResultDTO
                    }
                    .eraseToAnyPublisher()
    }
    
    private func mergeImages(_ taggedImages: TaggedImages) -> [APIImage] {
        let taggedImages = taggedImages.results?.map({ (taggedImagesResults) -> APIImage in
            return APIImage(filePath: taggedImagesResults.filePath)
        }) ?? []
        
        return taggedImages
    }
}
