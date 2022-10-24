//
//  Timeline.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

class Timeline: BaseLocalModel {
    var type: TimelineType!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        type                               <- (map["type"], EnumTransform<TimelineType>())
    }
    
    static func getTimeline(from dictionary: Dictionary<String, Any>, by timelineType: TimelineType) throws -> Timeline? {
        switch timelineType {
        case .item:
            return TimelineItem(JSON: dictionary)
        case .footer:
            return TimelineFooter(JSON: dictionary)
        case .title:
            return TimelineTitle(JSON: dictionary)
        }
    }
    
    static let exampleTitle = TimelineTitle(JSONString: "{\n      \"type\": \"title\",\n      \"title\": \"A História<br/>do Cinema:<br/>1895 a 1929\",\n      \"page_title\": \"1895 a 1929\",\n      \"next\": \"1930 a 1939\"\n    }")!
    
    static let exampleTitle2 = TimelineTitle(JSONString: "{\n      \"type\": \"title\",\n      \"title\": \"A História<br/>do Cinema:<br/>1930 a 1939\",\n      \"page_title\": \"1930 a 1939\",\n      \"coming_soon\": false,\n      \"previous\": \"1895 a 1929\",\n      \"next\": \"1940 a 1959\"\n    }")!
    
    static let exampleItem = TimelineItem(JSONString: "{\n      \"type\": \"item\",\n      \"year\": \"18\\n95\",\n      \"title\": \"A Primeira Exibição\",\n      \"description\": \"<a href=\\\"https://_{\'type\': \'screen\', \'id\': 1867, \'screen_type\': \'person\'}\\\">Auguste</a> (1862-1954) e <a href=\\\"https://_{\'type\': \'screen\', \'id\': 1868, \'screen_type\': \'person\'}\\\">Louis </a> (1864-1948) Lumière exibem 10 curtas-metragens em Paris para um público pagante, entre eles, <strong><a href=\\\"https://_{type: \'screen\', \'id\': 774, \'screen_type\': \'movie\'}\\\">A saída dos operários da fábrica Lumière</a></strong>.\",\n      \"image_transparent\": true,\n      \"image\": {\n        \"image_type\": \"local\",\n        \"url\": \"img_lumiere_brothers\",\n        \"content_description\": \"Foto de Auguste e Louis Lumière\",\n        \"style\": {\n          \"scale_type\": \"center_crop\"\n        }\n      }\n    }")!
    
    static let exampleFooter = TimelineFooter(JSONString: "{\n      \"type\": \"footer\",\n      \"next\": \"1930 a 1939\"\n    }")!
    
    static let exampleFooter2 = TimelineFooter(JSONString: "{\n      \"type\": \"footer\",\n      \"next\": \"1940 a 1959\",\n      \"previous\": \"1895 a 1929\"\n    }")!
}
