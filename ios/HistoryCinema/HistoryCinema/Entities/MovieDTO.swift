//
//  MovieDTO.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 05/10/22.
//

import Foundation

struct MovieDTO {
    let id: Int?
    let posterPath: String?
    let title: String?
    let subtitle: String?
    let year: Int?
    
    static func fromLocalMovie(_ movieLocal: LocalMovie, _ subtitle: String? = nil) -> MovieDTO {
        return MovieDTO(id: movieLocal.id, posterPath: movieLocal.posterPath, title: movieLocal.originalTitle, subtitle: subtitle, year: movieLocal.releaseDate?.yearInt)
    }
}
