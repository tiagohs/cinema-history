//
//  ContentType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/02/21.
//

import Foundation

enum ContentType: String {
    case text
    case gif
    case video
    case slide
    case image
    case audio_strem
    case quote
    case block_special
    case link_screen
    case movie_list
    case person_list
    case recomendations
    case awards_nominees
    case movie_list_special
    
    static func getContentType(by name: String) -> ContentType {
        switch name {
        case "text":
            return .text
        case "gif":
            return .gif
        case "video":
            return .video
        case "slide":
            return .slide
        case "image":
            return .image
        case "audio_strem":
            return .audio_strem
        case "quote":
            return .quote
        case "block_special":
            return .block_special
        case "link_screen":
            return .link_screen
        case "movie_list":
            return .movie_list
        case "person_list":
            return .person_list
        case "recomendations":
            return .recomendations
        case "awards_nominees":
            return .awards_nominees
        case "movie_list_special":
            return .movie_list_special
        default:
            return .text
        }
    }
}
