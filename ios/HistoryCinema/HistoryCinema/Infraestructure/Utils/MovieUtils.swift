//
//  MovieUtils.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import Foundation

class MovieUtils {
    
    static let allGenrerIDs = [
                28,
                12,
                16,
                35,
                80,
                99,
                18,
                10751,
                14,
                36,
                27,
                10402,
                9648,
                10749,
                878,
                10770,
                53,
                10752,
                37
    ]

    static let allGenrerNames = [
                "Ação",
                "Aventura",
                "Animação",
                "Comédia",
                "Crime",
                "Documentário",
                "Drama",
                "Família",
                "Fantasia",
                "Histórico",
                "Terror",
                "Musical",
                "Mistério",
                "Romance",
                "Ficção Ciêntifica",
                "TV",
                "Suspense",
                "Guerra",
                "Faroeste"
    ]
    
    static func getGenresName(ids: [Int]?) -> [String] {
        var genres: [String] = []
        var name = ""

        ids?.forEach { id in
            switch id {
            case 28:
                name = "Ação"
            case 12:
                name = "Aventura"
            case 16:
                name = "Animação"
            case 35:
                name = "Comédia"
            case 80:
                name = "Crime"
            case 99:
                name = "Documentário"
            case 18:
                name = "Drama"
            case 10751:
                name = "Família"
            case 14:
                name = "Fantasia"
            case 36:
                name = "Histórico"
            case 27:
                name = "Terror"
            case 10402:
                name = "Musical"
            case 9648:
                name = "Mistério"
            case 10749:
                name = "Romance"
            case 878:
                name = "Ficção Ciêntifica"
            case 10770:
                name = "TV"
            case 53:
                name = "Suspense"
            case 10752:
                name = "Guerra"
            case 37:
                name = "Faroeste"
            default:
                 name = ""
            }
            
            if !name.isEmpty {
                genres.append(name)
            }
        }
        
        return genres
    }
}
