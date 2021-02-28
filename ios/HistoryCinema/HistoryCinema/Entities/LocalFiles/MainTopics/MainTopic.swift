//
//  MainTopic.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class MainTopic: BaseLocalModel {
    var layoutType: MainTopicItemLayoutType!
    var mainTopicType: MainTopicsType!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        layoutType                               <- (map["layout_type"], EnumTransform<MainTopicItemLayoutType>())
        mainTopicType                            <- (map["main_topic_type"], EnumTransform<MainTopicsType>())
    }
    
    static func getMainTopic(from dictionary: Dictionary<String, Any>, by mainTopicsType: MainTopicsType, mainTopicItemLayoutType: MainTopicItemLayoutType) throws -> MainTopic? {
        if mainTopicItemLayoutType == .quote {
            return QuoteMainTopic(JSON: dictionary)
        }
        
        switch mainTopicsType {
        case .awards:
            return AwardMainTopic(JSON: dictionary)
        case .directors:
            return DirectorsMainTopic(JSON: dictionary)
        case .history_cinema:
            return MainTopicItem(JSON: dictionary)
        case .mil_movies:
            return MilMoviesMainTopic(JSON: dictionary)
        case .timeline:
            return MainTopic(JSON: dictionary)
        }
    }
    
    static func example(_ mainTopicType: MainTopicsType) -> MainTopic {
        switch mainTopicType {
        case .awards:
            return AwardMainTopic(JSONString: "{\n    \"main_topic_type\": \"awards\",\n    \"layout_type\": \"card_full\",\n    \"id\": 5,\n    \"name\": \"Prêmio do Sindicato dos Atores (SAG)\",\n    \"country\": \"Estados Unidos\",\n    \"presented_by\": \"Screen Actors Guild\",\n    \"first_awarded_date\": \"25/02/1995\",\n    \"nominees_id\": 5,\n    \"logo\": {\n      \"image_type\": \"local\",\n      \"url\": \"img_logo_sag_awards\",\n      \"content_description\": \"Foto com a logo do SAG.\",\n      \"style\": {\n        \"scale_type\": \"center_crop\"\n      }\n    },\n    \"image\": {\n      \"image_type\": \"local\",\n      \"url\": \"img_sag\",\n      \"content_description\": \"Foto com a estatueta do SAG.\",\n      \"style\": {\n        \"height\": 400,\n        \"scale_type\": \"center_crop\"\n      }\n    },\n    \"social_list\": [\n      {\n        \"type\": \"site\",\n        \"link\": \"https://www.sagawards.org/\"\n      },\n      {\n        \"type\": \"twitter\",\n        \"link\": \"https://twitter.com/sagawards\"\n      },\n      {\n        \"type\": \"instagram\",\n        \"link\": \"https://instagram.com/sagawards/\"\n      },\n      {\n        \"type\": \"facebook\",\n        \"link\": \"https://www.facebook.com/sagawardsofficialpage/\"\n      }\n    ]\n  }")!
        case .directors:
            return DirectorsMainTopic(JSONString: "{\n    \"main_topic_type\": \"directors\",\n    \"layout_type\": \"card_full\",\n    \"person_id\": 1071403,\n    \"title\": \"Alice Guy Blaché\",\n    \"image\": {\n      \"image_type\": \"local\",\n      \"url\": \"img_alice\",\n      \"content_description\": \"Fotografia de Alice Guy Blaché com uma camera.\",\n      \"style\": {\n        \"height\": 350,\n        \"resize\": {\n          \"height\": 350\n        },\n        \"scale_type\": \"center_crop\"\n      }\n    }\n  }")!
        case .history_cinema:
            return MainTopicItem(JSONString: "{\n          \"main_topic_type\": \"history_cinema\",\n          \"layout_type\": \"full\",\n          \"id\": 1,\n          \"title\": \"De 1895 a 1929\",\n          \"subtitle\": \"Parte 01\",\n          \"description\": \"Os visionários, inventores, sonhadores. As experimentações e descobertas de Georges Mélies, o humor único de Chaplin e Keaton, o surrealismo na Alemanha, a luta por uma voz com os Race Films, o romantismo e o espetáculo de Hollywood dos anos 20.\",\n          \"blocked\": false,\n          \"is_new\": false,\n          \"image\": {\n            \"image_type\": \"local\",\n            \"url\": \"img_voyage\",\n            \"content_description\": \"Cena do filme Viagem a Lua, onde uma lua, com feições humanas, é atingida no olho direito por um foguete.\",\n            \"animation\": {\n              \"type\": \"shake_vertical\",\n              \"duration\": 1300\n            },\n            \"style\": {\n              \"scale_type\": \"fit\",\n              \"resize\": {\n                \"height\": 250\n              }\n            }\n          },\n          \"presentation_image\": {\n            \"image_type\": \"local\",\n            \"url\": \"img_cabinet\",\n            \"content_description\": \"Cena do filme O gabinete do doutor Caligari, onde um homem verifica, desconfiado, o corpo de uma mulher no chão.\",\n            \"style\": {\n              \"scale_type\": \"center_crop\"\n            }\n          },\n          \"title_color\": \"md_white_1000\",\n          \"title_background_color\": \"md_black_1000\",\n          \"color\": \"md_red_500\",\n          \"quote_position\": \"bottom_start\",\n          \"quote\": {\n            \"quote\": \"Labor omnia vincit (o trabalho vence tudo)\",\n            \"author\": \"Viagem à Lua\",\n            \"text_color\": \"md_black_1000\",\n            \"background_color\": \"md_white_1000\",\n            \"icon_color\": \"md_grey_300\"\n          }\n        }")!
        case .mil_movies:
            return MilMoviesMainTopic(JSONString: "{\n    \"main_topic_type\": \"mil_movies\",\n    \"layout_type\": \"card_full\",\n    \"id\": 2,\n    \"list_id\": \"123876\",\n    \"title\": \"De 1930 a 1939\",\n    \"image\": {\n      \"image_type\": \"local\",\n      \"url\": \"img_frankenstein\",\n      \"content_description\": \"Foto promossional do filme Frankenstein, com o rosto triste do personagem Frankenstein.\",\n      \"style\": {\n        \"height\": 500,\n        \"scale_type\": \"center_crop\",\n        \"resize\": {\n          \"height\": 500\n        }\n      }\n    },\n    \"title_color\": \"md_yellow_900\",\n    \"background_color\": \"md_black_1000\"\n  }")!
        case .timeline:
            return MainTopic(JSONString: "{\n    \"main_topic_type\": \"timeline\",  }")!
        }
    }
}
