
import Foundation
import Combine
import Alamofire

// MARK: GenreService: GenreServiceInterface

class GenreService: GenreServiceProtocol {
    
    func getGenres(language: String = Locale.getCurrentAppLangAndRegion()) -> AnyPublisher<[Genre], AFError> {
        let url = TMDB.URL.GENRES.GENRES_LIST_URL
        let parameters = TMDB.URL.GENRES.buildPersonDetailsParameters(language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
                    .publishDecodable(type: GenreResult.self)
                    .value()
                    .map({ (genreResult) -> [Genre] in genreResult.genres ?? [] })
                    .eraseToAnyPublisher()
    }
    
    func getMoviesByGenre(genreId: Int, page: Int, language: String) -> AnyPublisher<ResultsMovie, AFError> {
        let url = TMDB.URL.GENRES.buildMovieListByGenreUrl(genreId)
        let parameters = TMDB.URL.GENRES.buildMovieListByGenreParameters(Locale.getCurrentAppRegion(), page, language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
                    .publishDecodable(type: ResultsMovie.self)
                    .value()
                    .eraseToAnyPublisher()
    }
}
