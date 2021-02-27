//
//  ReviewerType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation

enum ReviewerType: String {
    case omelete
    case plano_critico
    case adoro_cinema
    case unknown
    
    static func getReviwer(_ reviewerType: ReviewerType) -> Reviewer {
        switch reviewerType {
        case .omelete:
            return Reviewer(logo: "img_logo_omelete")
        case .plano_critico:
            return Reviewer(logo: "img_logo_plano_critico")
        case .adoro_cinema:
            return Reviewer(logo: "img_logo_adoro_cinema")
        case .unknown:
            return Reviewer(logo: "ic_placeholder_review")
        }
    }
}

struct Reviewer {
    var logo: String!
}
