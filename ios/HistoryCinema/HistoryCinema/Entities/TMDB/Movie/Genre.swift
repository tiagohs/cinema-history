
import Foundation

struct Genre : BaseModel {
	var id : Int?
	var name : String?
    var imageName : String?
    
    static func createGenresFromId(listOfId: [Int]) -> [Genre] {
        let localGenres = TMDB.GENRES_ID
        var finalGenres: [Genre] = []
        
        listOfId.forEach { (genreID) in
            if let value = localGenres[genreID] {
                let genre = Genre(id: genreID, name: value, imageName: nil)
                
                finalGenres.append(genre)
            }
        }
        
        return finalGenres
    }
}

struct GenreResult : BaseModel {
    var id: Int? = UUID().hashValue
    var genres : [Genre]?
}
