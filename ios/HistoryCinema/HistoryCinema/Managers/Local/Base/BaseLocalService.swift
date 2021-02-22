//
//  BaseLocalService.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation

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

    
}
