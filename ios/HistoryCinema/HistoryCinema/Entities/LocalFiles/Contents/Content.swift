//
//  Content.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation
import ObjectMapper

class Content: BaseLocalModel {
    var type: ContentType!
    
    required init?(map: Map) {
        super.init(map: map)
    }

    override func mapping(map: Map) {
        super.mapping(map: map)
        
        type <- (map["type"], EnumTransform<ContentType>())
    }
    
    static func getContent(from dictionary: Dictionary<String, Any>, by contentType: ContentType) throws -> Content? {
        switch contentType {
        case .text:
            return ContentText(JSON: dictionary)
        case .gif:
            return ContentGif(JSON: dictionary)
        case .video:
            return ContentVideo(JSON: dictionary)
        case .slide:
            return ContentSlide(JSON: dictionary)
        case .image:
            return ContentImage(JSON: dictionary)
        case .audio_strem:
            return ContentAudioStream(JSON: dictionary)
        case .quote:
            return ContentQuote(JSON: dictionary)
        case .block_special:
            return ContentBlockSpecial(JSON: dictionary)
        case .link_screen:
            return ContentLinkScreen(JSON: dictionary)
        case .movie_list:
            return ContentMovieList(JSON: dictionary)
        case .person_list:
            return ContentPersonList(JSON: dictionary)
        case .recomendations:
            return ContentRecomendation(JSON: dictionary)
        case .awards_nominees:
            return ContentNominee(JSON: dictionary)!
        case .movie_list_special:
            return ContentMovieListSpecial(JSON: dictionary)
        }
    }
}
