//
//  BaseLocalService.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import Combine
import Alamofire
import ObjectMapper

class BaseLocalService {
    
    func loadPages(_ filename: String) throws -> Page {
        guard let pageJSON = try filename.toJSONObject() as? Dictionary<String, Any> else {
            throw "Could't create JSONObject"
        }
        
        guard let contentList = pageJSON["content_list"] as? Array<Dictionary<String, Any>> else {
            throw "Could't find content_list key"
        }
        
        let page = Page(JSON: pageJSON)
        page?.contentList = try loadContent(contentList)
        
        return page!
    }
    
    func loadAwardsHistory(_ fileName: String) throws -> HistoryResult {
        guard let pageJSON = try fileName.toJSONObject() as? Dictionary<String, Any> else {
            throw "Could't create JSONObject"
        }
        
        guard let contentList = pageJSON["results"] as? Array<Dictionary<String, Any>> else {
            throw "Could't find awards history results key"
        }
        
        let historyResult = HistoryResult(JSON: pageJSON)
        historyResult?.results = try loadContent(contentList)
        
        return historyResult!
    }
    
    func loadGlossary(_ fileName: String) throws -> GlossaryResult {
        guard let glossaryResultJSON = try fileName.toJSONObject() as? Dictionary<String, Any> else {
            throw "Could't create glossary JSONObject"
        }
        
        guard let glossaryList = glossaryResultJSON["results"] as? Array<Dictionary<String, Any>> else {
            throw "Could't find glossariy result"
        }
        
        let glossaryResult = GlossaryResult(JSON: glossaryResultJSON)
        var i = 0
        
        glossaryResult?.results = try glossaryResult?.results?.map { resultItem in
            let resultDictionaryItem = glossaryList[i]
            
            guard let contentListDictionary = resultDictionaryItem["content_list"] as? Array<Dictionary<String, Any>> else {
                throw "Could't find glossary content in index \(i)"
            }
            
            resultItem.contentList = try loadContent(contentListDictionary)
            i += 1
            
            return resultItem
        }
        
        return glossaryResult!
    }
    
    func loadNominees(_ fileName: String) throws -> NomineeResult {
        guard let nomineeResultJSON = try fileName.toJSONObject() as? Dictionary<String, Any> else {
            throw "Could't create nominees JSONObject"
        }
        
        guard let resultList = nomineeResultJSON["results"] as? Array<Dictionary<String, Any>> else {
            throw "Could't find nominees result"
        }
        
        let nomineeResult = NomineeResult(JSON: nomineeResultJSON)
        var i = 0
        
        nomineeResult?.results = try nomineeResult?.results?.map { resultItem in
            let resultDictionaryItem = resultList[i]
            
            guard let contentListDictionary = resultDictionaryItem["content"] as? Array<Dictionary<String, Any>> else {
                throw "Could't find nominees content in index \(i)"
            }
            
            resultItem.content = try loadContent(contentListDictionary)
            i += 1
            
            return resultItem
        }
        
        return nomineeResult!
    }
    
    func loadReferencesContent(_ fileName: String) throws -> ReferencesResult {
        guard let referenceResultJSON = try fileName.toJSONObject() as? Dictionary<String, Any> else {
            throw "Could't create references JSONObject"
        }
        
        guard let resultList = referenceResultJSON["results"] as? Array<Dictionary<String, Any>> else {
            throw "Could't find references result"
        }
        
        let referenceResult = ReferencesResult(JSON: referenceResultJSON)
        var i = 0
        
        referenceResult?.results = try referenceResult?.results?.map { resultItem in
            let resultDictionaryItem = resultList[i]
            
            guard let contentListDictionary = resultDictionaryItem["references"] as? Array<Dictionary<String, Any>> else {
                throw "Could't find references content in index \(i)"
            }
            
            resultItem.references = try loadReferences(contentListDictionary)
            i += 1
            
            return resultItem
        }
        
        return referenceResult!
    }

    func loadContent(_ contentList: Array<Dictionary<String, Any>>) throws -> [Content] {
        var contents: [Content] = []
        
        try contentList.forEach { dictionary in
            guard let contentTypeName = dictionary["type"] as? String else {
                throw "Could't find content type name"
            }
            
            let contentType = ContentType.getContentType(by: contentTypeName)
            
            guard let content = try Content.getContent(from: dictionary, by: contentType) else {
                throw "Could't create Content"
            }
            
            contents.append(content)
        }
        
        return contents
    }
    
    func loadReferences(_ referenceList: Array<Dictionary<String, Any>>) throws -> [Reference] {
        var references: [Reference] = []
        
        try referenceList.forEach { dictionary in
            guard let referenceTypeName = dictionary["type"] as? String else {
                throw "Could't find reference type name"
            }
            
            let referenceType = ReferenceType.getReferenceType(by: referenceTypeName)
            
            guard let reference = try Reference.getReference(from: dictionary, by: referenceType) else {
                throw "Could't create Reference"
            }
            
            references.append(reference)
        }
        
        return references
    }

    func getLocalFile<T: Mappable>(_ fileName: String) -> AnyPublisher<T, AFError> {
        return Deferred {
                Future { promise in
                    guard let file = fileName.toFileURL() else {
                        promise(.failure(AFError.createURLRequestFailed(error: "Could't find \(fileName) in main bundle")))
                        return
                    }
                    
                    AF.request(file, method: .get)
                                .validate()
                                .responseObject { (response: AFDataResponse<T>) in
                                    promise(response.result)
                                }
                }
            }.eraseToAnyPublisher()
    }
}
