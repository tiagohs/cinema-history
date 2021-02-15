//
//  MovieServiceInterface.swift
//  popmovies
//
//  Created by Tiago Silva on 06/07/19.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//
import Combine
import Alamofire

protocol MovieServiceProtocol {
    
    func getMovieRankings(imdbId: String) -> AnyPublisher<MovieOMDB, AFError>
    func getDetails(movieId: Int, appendToResponse: [String], language: String) -> AnyPublisher<Movie, AFError>
    
    func getImages(movieId: Int, includeImageLanguage: [String], language: String?) -> AnyPublisher<Images, AFError>
    func getVideos(movieId: Int, language: String?) -> AnyPublisher<ResultsVideo, AFError>
    func getTranslations(movieId: Int) -> AnyPublisher<TranslationResults, AFError>
    
    func searchMovie(with query: String, page: Int, region: String) -> AnyPublisher<ResultsMovie, AFError>
    
    func getMovieDiscover(page: Int, discoverMovie: DiscoverMovie, language: String) -> AnyPublisher<ResultsMovie, AFError>
    func getPopularMovies(page: Int, region: String) -> AnyPublisher<ResultsMovie, AFError>
    func getNowPlaying(page: Int, region: String) -> AnyPublisher<ResultsMovie, AFError>
    func getTopRated(page: Int, region: String) -> AnyPublisher<ResultsMovie, AFError>
    func getUpcoming(page: Int, region: String) -> AnyPublisher<ResultsMovie, AFError>
    func getMovieList(url: String, parameters: [String : String]) -> AnyPublisher<ResultsMovie, AFError>
}
