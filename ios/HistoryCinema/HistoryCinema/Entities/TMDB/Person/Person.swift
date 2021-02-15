//
//  Person.swift
//  popmovies
//
//  Created by Tiago Silva on 19/06/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation
import ObjectMapper

struct Person: BaseModel {
    var id : Int?
    var birthday : Date?
    var knownForDepartment : String?
    var placeOfBirth : String?
    var homepage : String?
    var profilePath : String?
    var imdbId : String?
    var deathday : String?
    var name : String?
    var alsoKnownAs : [String]?
    var biography : String?
    var adult : Bool?
    var gender : Int?
    var popularity : Double?
    
    // Append To Reponse
    var images : Images?
    var externalIds : ExternalIds?
    var movieCredits : MovieCredits?
    var taggedImages : TaggedImages?
    
    //Custom
    var allMovieCredits : [Movie] = []
    var allImages : [Image] = []
    
    enum CodingKeys: String, CodingKey {
        case id, birthday, homepage, deathday, images, name, biography, adult, gender, popularity
        case knownForDepartment = "known_for_department"
        case placeOfBirth = "place_of_birth"
        case profilePath = "profile_path"
        case imdbId = "imdb_id"
        case externalIds = "external_ids"
        case alsoKnownAs = "also_known_as"
        case movieCredits = "movie_credits"
        case taggedImages = "tagged_images"
    }
    
//    mutating func mergeMovies() -> [Movie] {
//        let castCredits = self.movieCredits?.cast?.map({ (creditCast) -> Movie in
//            let movie = Movie(id: creditCast.id, title: creditCast.title, posterPath: creditCast.posterPath, backdropPath: creditCast.backdropPath, releaseDate: creditCast.releaseDate)
//            
//            return movie
//        }) ?? []
//        let crewCredits = self.movieCredits?.crew?.map({ (creditCrew) -> Movie in
//            let movie = Movie()
//            
//            movie.id = creditCrew.id
//            movie.title = creditCrew.title
//            movie.posterPath = creditCrew.posterPath
//            movie.backdropPath = creditCrew.backdropPath
//            movie.releaseDate = creditCrew.releaseDate
//            
//            return movie
//        }) ?? []
//        
//        return Array(Set(castCredits + crewCredits))
//    }
    
    func mergeImages() -> [Image] {
        let taggedImages = self.taggedImages?.results?.map({ (taggedImagesResults) -> Image in
            return Image(filePath: taggedImagesResults.filePath)
        }) ?? []
        let profileImages = images?.profiles ?? []
        
        return Array(Set(taggedImages + profileImages))
    }
}
