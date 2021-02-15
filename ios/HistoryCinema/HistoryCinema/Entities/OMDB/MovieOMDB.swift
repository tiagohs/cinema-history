//
//  MovieOMDB.swift
//  popmovies
//
//  Created by Tiago Silva on 21/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import ObjectMapper
import Foundation

struct MovieOMDB: BaseModel {
    var id: Int? = UUID().hashValue
    var title : String?
    var year : String?
    var rated : String?
    var released : String?
    var runtime : String?
    var genre : String?
    var director : String?
    var writer : String?
    var actors : String?
    var plot : String?
    var language : String?
    var country : String?
    var awards : String?
    var poster : String?
    var ratings : [Rating]?
    var metascore : String?
    var imdbRating : String?
    var imdbVotes : String?
    var imdbID : String?
    var type : String?
    var dVD : String?
    var boxOffice : String?
    var production : String?
    var website : String?
    var response : String?
    var tomatoURL: String?
    
    var tomatoesRating: Rating? {
        return self.ratings?.first(
            where: { (rating) -> Bool in return rating.source == Rating.TOMATOES_SOURCE_KEY })
    }
    
    var internetMovieDatabaseRating: Rating? {
        return self.ratings?.first(
            where: { (rating) -> Bool in return rating.source == Rating.INTERNET_MOVIE_SOURCE_KEY })
    }
    
    var metacriticRating: Rating? {
        return self.ratings?.first(
            where: { (rating) -> Bool in return rating.source == Rating.METACRITIC_SOURCE_KEY })
    }
    
    enum CodingKeys: String, CodingKey {
        case title = "Title"
        case year = "Year"
        case rated = "Rated"
        case released = "Released"
        case runtime = "Runtime"
        case genre = "Genre"
        case director = "Director"
        case writer = "Writer"
        case actors = "Actors"
        case plot = "Plot"
        case language = "Language"
        case country = "Country"
        case awards = "Awards"
        case poster = "Poster"
        case ratings = "Ratings"
        case metascore = "Metascore"
        case imdbRating = "imdbRating"
        case imdbVotes = "imdbVotes"
        case imdbID = "imdbID"
        case type = "Type"
        case dVD = "DVD"
        case boxOffice = "BoxOffice"
        case production = "Production"
        case website = "Website"
        case response = "Response"
        case tomatoURL = "tomatoURL"
    }
    
}
