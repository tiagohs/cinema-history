//
//  MovieService.swift
//  popmovies
//
//  Created by Tiago Silva on 14/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation
import Combine
import Alamofire

// MARK: MovieService: MovieServiceInterface

class MovieService: MovieServiceProtocol {
    
    func getMovieRankings(imdbId: String) -> AnyPublisher<MovieOMDB, AFError> {
        let url = OMDB.BASE_URL
        let parameters = OMDB.URL.buildMovieRankingsParameters(imdbId)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: MovieOMDB.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getDetails(movieId: Int, appendToResponse: [String], language: String = Locale.getCurrentAppLangAndRegion()) -> AnyPublisher<Movie, AFError> {
        let url = TMDB.URL.MOVIES.buidMovieDetailsUrl(movieId: movieId)
        let parameters = TMDB.URL.MOVIES.buildMovieDetailsParameters(appendToResponse, language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: Movie.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getImages(movieId: Int, includeImageLanguage: [String], language: String? = nil) -> AnyPublisher<Images, AFError> {
        let url = TMDB.URL.MOVIES.buildImagesUrl(movieId: movieId)
        let parameters = TMDB.URL.MOVIES.buildImagesParameters(includeImageLanguage, language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: Images.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getImages(movieId: Int, language: String) -> AnyPublisher<Images, AFError> {
        let url = TMDB.URL.MOVIES.buildImagesUrl(movieId: movieId)
        let parameters = TMDB.URL.MOVIES.buildImagesParameters(language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: Images.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getVideos(movieId: Int, language: String? = nil) -> AnyPublisher<ResultsVideo, AFError> {
        let url = TMDB.URL.MOVIES.buidVideosUrl(movieId: movieId)
        let parameters = TMDB.URL.MOVIES.buildVideosParameters(language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: ResultsVideo.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getTranslations(movieId: Int) -> AnyPublisher<TranslationResults, AFError> {
        let url = TMDB.URL.MOVIES.buildTranslationsMoviesUrl(movieId: movieId)
        let parameters = TMDB.URL.MOVIES.buildTranslationsParameters()
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: TranslationResults.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func searchMovie(with query: String, page: Int, region: String = Locale.getCurrentAppRegion()) -> AnyPublisher<ResultsMovie, AFError> {
        let url = TMDB.URL.MOVIES.SEARCH_MOVIES_URL
        let parameters = TMDB.URL.MOVIES.buildSearchParameters(query, region, page)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: ResultsMovie.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getMovieDiscover(page: Int, discoverMovie: DiscoverMovie, language: String) -> AnyPublisher<ResultsMovie, AFError> {
        let url = TMDB.URL.MOVIES.DISCOVER_MOVIES_URL
        let parameters = TMDB.URL.MOVIES.buildMovieDiscoverParameters(discoverMovie, page, language)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: ResultsMovie.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    
    func getList(listId: String, page: Int) -> AnyPublisher<ResultsList, AFError> {
        let url = TMDB.URL.MOVIES.buidMovieListUrl(listId: listId)
        let parameters = TMDB.URL.MOVIES.buildMovieListParameters(page: page)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: ResultsList.self)
            .value()
            .eraseToAnyPublisher()
    }
    
    func getSimilarMovies(movieId: Int, page: Int, region: String = Locale.getCurrentAppRegion()) -> AnyPublisher<ResultsMovie, AFError> {
        let url = TMDB.URL.MOVIES.buildSimilarMoviesUrl(movieId: movieId)
        
        return buildMovieList(url: url, region: region, page: page)
    }
    
    func getNowPlaying(page: Int, region: String = Locale.getCurrentAppRegion()) -> AnyPublisher<ResultsMovie, AFError> {
        let url = TMDB.URL.MOVIES.NOW_PLAYING_MOVIES_URL
        
        return buildMovieList(url: url, region: region, page: page)
    }
    
    func getTopRated(page: Int, region: String = Locale.getCurrentAppRegion()) -> AnyPublisher<ResultsMovie, AFError> {
        let url = TMDB.URL.MOVIES.TOP_RATED_MOVIES_URL
        
        return buildMovieList(url: url, region: region, page: page)
    }
    
    func getUpcoming(page: Int, region: String = Locale.getCurrentAppRegion()) -> AnyPublisher<ResultsMovie, AFError> {
        let url = TMDB.URL.MOVIES.UPCOMING_MOVIES_URL
        
        return buildMovieList(url: url, region: region, page: page)
    }
    
    func getPopularMovies(page: Int, region: String = Locale.getCurrentAppRegion()) -> AnyPublisher<ResultsMovie, AFError> {
        let url = TMDB.URL.MOVIES.POPULAR_MOVIES_URL
        
        return buildMovieList(url: url, region: region, page: page)
    }
}

// MARK: Helpers Method

private extension MovieService {
    
    private func buildMovieList(url: String, region: String, page: Int) -> AnyPublisher<ResultsMovie, AFError> {
        let parameters = TMDB.URL.MOVIES.buildMovieListParameters(region: region, page: page)
        
        return AF.request(url, method: .get, parameters: parameters)
            .responseJSON { response in DebugUtils.debug(response) }
            .publishDecodable(type: ResultsMovie.self)
            .value()
            .eraseToAnyPublisher()
    }
}
