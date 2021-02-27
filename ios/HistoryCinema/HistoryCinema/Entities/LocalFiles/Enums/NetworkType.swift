//
//  NetworkType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import ObjectMapper

enum NetworkType: String {
    case prime_video
    case youtube
    case kanopy
    case apple_tv
    case amazon_video
    case netflix
    case hbo_max
    case hbo_go
    case looke
    case globo_play
    case play_store
    case telecine
    case claro_video
    case mubi
    case vivo_play
    case criterionchannel
    case fandor
    case now
    case uol_play
    case dailymotion
    case unknown
    
    static func getNetwork(_ networkType: NetworkType) -> Network {
        switch networkType {
        case .prime_video:
            return Network(type: "prime_video", color: "prime_video_color", textColor: "md_white_1000", networkName: "Amazon Prime Video")
        case .youtube:
            return Network(type: "youtube", color: "youtube_color", textColor: "md_white_1000", networkName: "Youtube")
        case .kanopy:
            return Network(type: "kanopy", color: "kanopy_color", textColor: "md_white_1000", networkName: "Kanopy")
        case .apple_tv:
            return Network(type: "apple_tv", color: "apple_tv_color", textColor: "md_white_1000", networkName: "Apple TV")
        case .amazon_video:
            return Network(type: "amazon_video", color: "md_black_1000", textColor: "amazon_color", networkName: "Amazon Video")
        case .netflix:
            return Network(type: "netflix", color: "netflix", textColor: "md_white_1000", networkName: "Netflix")
        case .hbo_max:
            return Network(type: "hbo_max", color: "hbo_max", textColor: "md_white_1000", networkName: "HBO Max")
        case .hbo_go:
            return Network(type: "hbo_go", color: "md_black_1000", textColor: "md_white_1000", networkName: "HBO GO")
        case .looke:
            return Network(type: "looke", color: "looke", textColor: "md_white_1000", networkName: "Looke")
        case .globo_play:
            return Network(type: "globo_play", color: "globo_play", textColor: "md_white_1000", networkName: "Globoplay")
        case .play_store:
            return Network(type: "play_store", color: "play_store", textColor: "md_white_1000", networkName: "Google Play")
        case .telecine:
            return Network(type: "telecine", color: "telecine", textColor: "md_white_1000", networkName: "Telecine Play")
        case .claro_video:
            return Network(type: "claro_video", color: "claro_video", textColor: "md_white_1000", networkName: "Claro Vídeo")
        case .mubi:
            return Network(type: "mubi", color: "mubi", textColor: "md_white_1000", networkName: "Mubi")
        case .vivo_play:
            return Network(type: "vivo_play", color: "vivo_play", textColor: "md_white_1000", networkName: "Vivo Play")
        case .criterionchannel:
            return Network(type: "criterionchannel", color: "criterionchannel", textColor: "md_white_1000", networkName: "The Criterion Channel")
        case .fandor:
            return Network(type: "fandor", color: "fandor", textColor: "md_black_1000", networkName: "Fandor")
        case .now:
            return Network(type: "now", color: "now", textColor: "md_white_1000", networkName: "NOW")
        case .uol_play:
            return Network(type: "uol_play", color: "uol_play", textColor: "md_white_1000", networkName: "UOL Play")
        case .dailymotion:
            return Network(type: "dailymotion", color: "dailymotion", textColor: "md_white_1000", networkName: "Dailymotion")
        case .unknown:
            return Network(type: "unknown", color: "md_black_1000", textColor: "md_white_1000")
        }
    }
}

struct Network {
    var type: String!
    var color: String!
    var textColor: String!
    var networkName: String?
}
