//
//  Movie.swift
//  popmovies
//
//  Created by Tiago Silva on 14/04/2019.
//  Copyright © 2019 Tiago Silva. All rights reserved.
//

import Foundation

struct Movie: BaseModel {
    var id: Int?
    var adult: Bool?
    var backdropPath: String?
    var budget: Int?
    var homepage: String?
    var imdbID, originalLanguage, originalTitle, overview: String?
    var popularity: Decimal?
    var posterPath: String?
    var revenue, runtime: Int?
    var status, tagline, title: String?
    var video: Bool?
    var voteCount: Int?
    private var release_date: String?
    var releaseDate: String?
    var genreIds: [Int]?
    
    var genres: [Genre]?
    var productionCompanies: [ProductionCompany]?
    var productionCountries: [ProductionCountry]?
    var spokenLanguages: [SpokenLanguage]?
    
    // Append To Reponse
    var alternativeTitles: [AlternativeTitle]?
    var credits: MediaCreditList?
    var images: Images?
    var keywords: ResultsKeyword?
    var releases: Release?
    var videos: VideoResult?
    var translations: TranslationResults?
    var similiarMovies: ResultsMovie?
    
    var allImages: [APIImage] = []
    var isFavorite: Bool = false
    var isWatched: Bool = false
    var isWantToSee: Bool = false
    var movieRankings: MovieOMDB? = nil
    
    func originalTitleWithYear() -> String? {
        if let originalTitle = originalTitle {
            var finalOriginalTitle = originalTitle
            
            if let year = releaseDate?.toDate()?.year {
                finalOriginalTitle += " (\(year)"
            }
            
            return finalOriginalTitle
        }
        
        return originalTitle
    }
    
    func directors() -> String? {
        if let crew = credits?.crew {
            return crew.filter { crew in return crew.job == "Director" }
                .map { crew in crew.name ?? "" }.joined(separator: ", ")
        }
        
        return nil
    }
    
    func writers() -> String? {
        if let crew = credits?.crew {
            return crew.filter { crew in return crew.job == "Screenplay" || crew.job == "Writer" || crew.job == "Writing" }
                .map { crew in crew.name ?? "" }.joined(separator: ", ")
        }
        
        return nil
    }
    
    func language() -> String? {
        if let originalLanguageIdentifier = self.originalLanguage {
            return Locale(identifier: originalLanguageIdentifier).localizedString(forIdentifier: originalLanguageIdentifier)?.capitalized
        }
        
        return nil
    }
    
    func duration() -> String? {
        if let runtime = self.runtime {
            let hours = abs(runtime / 60)
            let minutes = abs(runtime) % 60
            
            return "\(hours)h \(minutes)m"
        }
        
        return nil
    }
    
    enum CodingKeys: String, CodingKey {
        case id, adult, budget, homepage, overview, popularity, revenue, runtime, status, title, video, credits, images, keywords, releases, videos, translations
        case releaseDate = "release_date"
        case backdropPath = "backdrop_path"
        case genreIds = "genre_ids"
        case imdbID = "imdb_id"
        case originalLanguage = "original_language"
        case originalTitle = "original_title"
        case posterPath = "poster_path"
        case voteCount = "vote_count"
        case genres = "genres"
        
        case productionCompanies = "production_companies"
        case productionCountries = "production_countries"
        case spokenLanguages = "spoken_languages"
        
        case alternativeTitles = "alternative_titles"
        case similiarMovies = "similar_movies"
    }
    
    private func mergeImages() -> [APIImage] {
        let posters = self.images?.backdrops ?? []
        let backdrop = self.images?.posters ?? []
        
        if (posters.count > 3 && backdrop.count > 3) {
            return Array(backdrop[0..<3]) + Array(posters[0..<3])
        }
        
        return Array(Set(backdrop + posters))
    }
    
    static func == (lhs: Movie, rhs: Movie) -> Bool {
        return (lhs.id == rhs.id)
    }
    
    static let exampleMovie = try! JSONDecoder().decode(Movie.self, from: "{\"adult\":false,\"backdrop_path\":\"/kJSnhBQZyCcbqxMZJuDby8ULcfS.jpg\",\"genre_ids\":[12,878],\"id\":775,\"media_type\":\"movie\",\"original_language\":\"fr\",\"original_title\":\"Le Voyage dans la Lune\",\"overview\":\"Professor Barbenfouillis and five of his colleagues from the Academy of Astronomy travel to the Moon aboard a rocket propelled by a giant cannon. Once on the lunar surface, the bold explorers face the many perils hidden in the caves of the mysterious planet.\",\"popularity\":19.8,\"poster_path\":\"/9o0v5LLFk51nyTBHZSre6OB37n2.jpg\",\"release_date\":\"1902-10-04\",\"title\":\"A Trip to the Moon\",\"video\":false,\"vote_average\":7.9,\"vote_count\":1377}".data(using: .utf8)!)
    
    static func exampleMovieFull() -> Movie {
        var movie = try! JSONDecoder().decode(Movie.self, from: "{\n   \"adult\":false,\n   \"backdrop_path\":\"/kJSnhBQZyCcbqxMZJuDby8ULcfS.jpg\",\n   \"belongs_to_collection\":null,\n   \"budget\":5985,\n   \"genres\":[\n      {\n         \"id\":12,\n         \"name\":\"Adventure\"\n      },\n      {\n         \"id\":878,\n         \"name\":\"Science Fiction\"\n      }\n   ],\n   \"homepage\":\"\",\n   \"id\":775,\n   \"imdb_id\":\"tt0000417\",\n   \"original_language\":\"fr\",\n   \"original_title\":\"Le Voyage dans la Lune\",\n   \"overview\":\"Professor Barbenfouillis and five of his colleagues from the Academy of Astronomy travel to the Moon aboard a rocket propelled by a giant cannon. Once on the lunar surface, the bold explorers face the many perils hidden in the caves of the mysterious planet.\",\n   \"popularity\":16.671,\n   \"poster_path\":\"/9o0v5LLFk51nyTBHZSre6OB37n2.jpg\",\n   \"production_companies\":[\n      {\n         \"id\":7159,\n         \"logo_path\":null,\n         \"name\":\"Star-Film\",\n         \"origin_country\":\"FR\"\n      }\n   ],\n   \"production_countries\":[\n      {\n         \"iso_3166_1\":\"FR\",\n         \"name\":\"France\"\n      }\n   ],\n   \"release_date\":\"1902-04-17\",\n   \"revenue\":0,\n   \"runtime\":15,\n   \"spoken_languages\":[\n      {\n         \"english_name\":\"No Language\",\n         \"iso_639_1\":\"xx\",\n         \"name\":\"No Language\"\n      }\n   ],\n   \"status\":\"Released\",\n   \"tagline\":\"\",\n   \"title\":\"A Trip to the Moon\",\n   \"video\":false,\n   \"vote_average\":7.944,\n   \"vote_count\":1378,\n   \"keywords\":{\n      \"keywords\":[\n         {\n            \"id\":305,\n            \"name\":\"moon\"\n         },\n         {\n            \"id\":818,\n            \"name\":\"based on novel or book\"\n         },\n         {\n            \"id\":8201,\n            \"name\":\"satire\"\n         },\n         {\n            \"id\":10222,\n            \"name\":\"astronomer\"\n         },\n         {\n            \"id\":154802,\n            \"name\":\"silent film\"\n         },\n         {\n            \"id\":181239,\n            \"name\":\"scientific expedition\"\n         },\n         {\n            \"id\":195114,\n            \"name\":\"space adventure\"\n         },\n         {\n            \"id\":232797,\n            \"name\":\"early cinema\"\n         },\n         {\n            \"id\":257758,\n            \"name\":\"selenite\"\n         },\n         {\n            \"id\":263548,\n            \"name\":\"short film\"\n         }\n      ]\n   },\n   \"videos\":{\n      \"results\":[\n         {\n            \"iso_639_1\":\"en\",\n            \"iso_3166_1\":\"US\",\n            \"name\":\"George Méliès\' A Trip to the Moon Official Trailer HD\",\n            \"key\":\"JEGIyo-dKmA\",\n            \"site\":\"YouTube\",\n            \"size\":1080,\n            \"type\":\"Trailer\",\n            \"official\":false,\n            \"published_at\":\"2020-06-22T10:34:18.000Z\",\n            \"id\":\"5ef08b56283ed900368e07a4\"\n         },\n         {\n            \"iso_639_1\":\"en\",\n            \"iso_3166_1\":\"US\",\n            \"name\":\"1902 Georges Méliès Le voyage dans la lune A trip to the moon Short movie court Silent Muet\",\n            \"key\":\"bv9VMmDFQZs\",\n            \"site\":\"YouTube\",\n            \"size\":360,\n            \"type\":\"Clip\",\n            \"official\":false,\n            \"published_at\":\"2020-01-04T17:16:03.000Z\",\n            \"id\":\"5e13bf74d64ac2001466d04f\"\n         }\n      ]\n   },\n   \"external_ids\":{\n      \"imdb_id\":\"tt0000417\",\n      \"facebook_id\":null,\n      \"instagram_id\":null,\n      \"twitter_id\":null\n   },\n   \"translations\":{\n      \"translations\":[\n         {\n            \"iso_3166_1\":\"ES\",\n            \"iso_639_1\":\"ca\",\n            \"name\":\"Català\",\n            \"english_name\":\"Catalan\",\n            \"data\":{\n               \"homepage\":\"\",\n               \"overview\":\"Sis valents astronautes viatgen en una càpsula espacial de la Terra a la Lluna. La primera pel·lícula de ciència-ficció de la història va ser obra de la imaginació del director francès i mag Georges Méliès (1861-1938), que es va inspirar en les obres \\\"From the Earth to the Moon\\\" (1865) de Julio Verne i \\\"First Men in the Moon\\\" (1901) d\'H. G. Wells. Es tracta d\'un curtmetratge de 14 minuts de durada realitzat amb l\'astronòmic pressupost per a l\'època de 10.000 francs, suposava gens menys que la pel·lícula número 400 del realitzador francès, i obria al món del cinema una nova porta per explicar històries fantàstiques i de ciència-ficció mitjançant l\'ús de trucs i efectes especials. En 2011 la pel·lícula va ser reestrenada a Cannes en un programa doble al costat del documental \\\"Li Voyage Extraordinaire\\\". La versió restaurada i en color del curtmetratge de 1902 incloïa a més escenes extra i una altra banda sonora.\",\n               \"runtime\":14,\n               \"tagline\":\"\",\n               \"title\":\"Viatge a la Lluna\"\n            }\n         },\n         {\n            \"iso_3166_1\":\"CZ\",\n            \"iso_639_1\":\"cs\",\n            \"name\":\"Český\",\n            \"english_name\":\"Czech\",\n            \"data\":{\n               \"homepage\":\"\",\n               \"overview\":\"Šest vědců se vydává na expedici na Měsíc. Cestují v raketě tvaru náboje, vystřelené na Měsíc z obřího kanónu. Po bezpečném přistání se setkávají s Měsíčany, Selenity, poté uniknou před jejich králem a vrátí se na Zemi, kde jsou po dopadu do moře vyloveni jedním námořníkem.  Nikdo si nedokázal představit, že tento nejslavnější Mélièsův film stále existuje v barevné verzi. Lobster Films, Nadace Groupama Gan pro kinematografii a Nadace Technicolor pro filmové dědictví zahájili v roce 2010 kompletní restauraci této kopie.\",\n               \"runtime\":14,\n               \"tagline\":\"\",\n               \"title\":\"Cesta na Měsíc\"\n            }\n         }\n      ]\n   },\n   \"images\":{\n      \"backdrops\":[\n         {\n            \"aspect_ratio\":1.777,\n            \"height\":1554,\n            \"iso_639_1\":null,\n            \"file_path\":\"/kJSnhBQZyCcbqxMZJuDby8ULcfS.jpg\",\n            \"vote_average\":5.252,\n            \"vote_count\":4,\n            \"width\":2762\n         },\n         {\n            \"aspect_ratio\":1.778,\n            \"height\":720,\n            \"iso_639_1\":null,\n            \"file_path\":\"/g67r1eiQD3ERSEQFCFkSn7TeGw5.jpg\",\n            \"vote_average\":5.246,\n            \"vote_count\":2,\n            \"width\":1280\n         },\n         {\n            \"aspect_ratio\":1.778,\n            \"height\":2160,\n            \"iso_639_1\":null,\n            \"file_path\":\"/cA1dNxtjG0IJneHk6LhdZj5JSU7.jpg\",\n            \"vote_average\":5.246,\n            \"vote_count\":2,\n            \"width\":3840\n         },\n         {\n            \"aspect_ratio\":1.778,\n            \"height\":1144,\n            \"iso_639_1\":null,\n            \"file_path\":\"/jeC1kzwWnZTuXl7xF4E5D70BD8c.jpg\",\n            \"vote_average\":5.19,\n            \"vote_count\":5,\n            \"width\":2034\n         }\n      ],\n      \"logos\":[\n         {\n            \"aspect_ratio\":1.45,\n            \"height\":1226,\n            \"iso_639_1\":\"en\",\n            \"file_path\":\"/5Kyxg4JjWbSHeJcsWMMOyBN3vxT.png\",\n            \"vote_average\":5.312,\n            \"vote_count\":1,\n            \"width\":1778\n         },\n         {\n            \"aspect_ratio\":1.848,\n            \"height\":290,\n            \"iso_639_1\":\"en\",\n            \"file_path\":\"/dvqwK76xofDu2ATcoXMbEEDRx9N.png\",\n            \"vote_average\":0.0,\n            \"vote_count\":0,\n            \"width\":536\n         },\n         {\n            \"aspect_ratio\":1.449,\n            \"height\":247,\n            \"iso_639_1\":\"en\",\n            \"file_path\":\"/oHGlcJ94BWaOgoYNLthvlcLPgi6.png\",\n            \"vote_average\":0.0,\n            \"vote_count\":0,\n            \"width\":358\n         },\n         {\n            \"aspect_ratio\":1.753,\n            \"height\":554,\n            \"iso_639_1\":\"en\",\n            \"file_path\":\"/zMVcBzawZvph2KKnX2bAkSunysp.png\",\n            \"vote_average\":0.0,\n            \"vote_count\":0,\n            \"width\":971\n         }\n      ],\n      \"posters\":[\n         {\n            \"aspect_ratio\":0.667,\n            \"height\":3000,\n            \"iso_639_1\":\"en\",\n            \"file_path\":\"/9o0v5LLFk51nyTBHZSre6OB37n2.jpg\",\n            \"vote_average\":5.388,\n            \"vote_count\":4,\n            \"width\":2000\n         }\n      ]\n   },\n   \"releases\":{\n      \"countries\":[\n         {\n            \"certification\":\"\",\n            \"iso_3166_1\":\"JP\",\n            \"primary\":false,\n            \"release_date\":\"2017-02-04\"\n         },\n         {\n            \"certification\":\"\",\n            \"iso_3166_1\":\"JP\",\n            \"primary\":false,\n            \"release_date\":\"2015-09-22\"\n         }\n      ]\n   },\n   \"credits\":{\n      \"cast\":[\n         {\n            \"adult\":false,\n            \"gender\":2,\n            \"id\":11523,\n            \"known_for_department\":\"Directing\",\n            \"name\":\"Georges Méliès\",\n            \"original_name\":\"Georges Méliès\",\n            \"popularity\":5.421,\n            \"profile_path\":\"/acx5saf22Qj6FV5WAsacRHlZAMn.jpg\",\n            \"cast_id\":11,\n            \"character\":\"Professor Barbenfouillis / The Moon\",\n            \"credit_id\":\"52fe4275c3a36847f8020189\",\n            \"order\":0\n         },\n         {\n            \"adult\":false,\n            \"gender\":1,\n            \"id\":11645,\n            \"known_for_department\":\"Acting\",\n            \"name\":\"Bleuette Bernon\",\n            \"original_name\":\"Bleuette Bernon\",\n            \"popularity\":1.788,\n            \"profile_path\":\"/u14pGZRIO0Ylsnm9tyimI6I5gbO.jpg\",\n            \"cast_id\":13,\n            \"character\":\"Lady in the Moon (uncredited)\",\n            \"credit_id\":\"52fe4275c3a36847f8020191\",\n            \"order\":1\n         }\n      ],\n      \"crew\":[\n         {\n            \"adult\":false,\n            \"gender\":2,\n            \"id\":2088,\n            \"known_for_department\":\"Writing\",\n            \"name\":\"Jules Verne\",\n            \"original_name\":\"Jules Verne\",\n            \"popularity\":5.048,\n            \"profile_path\":\"/vdbQLwcvF3UNTJw9Z2OgQzGzoGx.jpg\",\n            \"credit_id\":\"52fe4275c3a36847f8020161\",\n            \"department\":\"Writing\",\n            \"job\":\"Novel\"\n         },\n         {\n            \"adult\":false,\n            \"gender\":2,\n            \"id\":11523,\n            \"known_for_department\":\"Directing\",\n            \"name\":\"Georges Méliès\",\n            \"original_name\":\"Georges Méliès\",\n            \"popularity\":5.421,\n            \"profile_path\":\"/acx5saf22Qj6FV5WAsacRHlZAMn.jpg\",\n            \"credit_id\":\"52fe4275c3a36847f8020173\",\n            \"department\":\"Writing\",\n            \"job\":\"Screenplay\"\n         },\n         {\n            \"adult\":false,\n            \"gender\":2,\n            \"id\":11523,\n            \"known_for_department\":\"Directing\",\n            \"name\":\"Georges Méliès\",\n            \"original_name\":\"Georges Méliès\",\n            \"popularity\":5.421,\n            \"profile_path\":\"/acx5saf22Qj6FV5WAsacRHlZAMn.jpg\",\n            \"credit_id\":\"52fe4275c3a36847f802015b\",\n            \"department\":\"Directing\",\n            \"job\":\"Director\"\n         },\n         {\n            \"adult\":false,\n            \"gender\":2,\n            \"id\":11523,\n            \"known_for_department\":\"Directing\",\n            \"name\":\"Georges Méliès\",\n            \"original_name\":\"Georges Méliès\",\n            \"popularity\":5.421,\n            \"profile_path\":\"/acx5saf22Qj6FV5WAsacRHlZAMn.jpg\",\n            \"credit_id\":\"56b8dc6ec3a368746b000880\",\n            \"department\":\"Editing\",\n            \"job\":\"Editor\"\n         }\n      ]\n   },\n   \"similar_movies\":{\n      \"page\":1,\n      \"results\":[\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/vhamaqMAkSphsGGAYt5ggeuPGK6.jpg\",\n            \"genre_ids\":[\n               18\n            ],\n            \"id\":3558,\n            \"original_language\":\"en\",\n            \"original_title\":\"Girl, Interrupted\",\n            \"overview\":\"Set in the changing world of the late 1960s, Susanna Kaysen\'s prescribed \\\"short rest\\\" from a psychiatrist she had met only once becomes a strange, unknown journey into Alice\'s Wonderland, where she struggles with the thin line between normal and crazy. Susanna soon realizes how hard it is to get out once she has been committed, and she ultimately has to choose between the world of people who belong inside or the difficult world of reality outside.\",\n            \"popularity\":35.225,\n            \"poster_path\":\"/dOBdatHIVppvmRFw2z7bf9VKJr9.jpg\",\n            \"release_date\":\"1999-03-24\",\n            \"title\":\"Girl, Interrupted\",\n            \"video\":false,\n            \"vote_average\":7.557,\n            \"vote_count\":3072\n         },\n         {\n            \"adult\":false,\n            \"backdrop_path\":\"/dompWVgGL1qZ84BkQMlxGwOhKb8.jpg\",\n            \"genre_ids\":[\n               10749,\n               35,\n               18\n            ],\n            \"id\":3573,\n            \"original_language\":\"en\",\n            \"original_title\":\"Emma\",\n            \"overview\":\"Emma Woodhouse is a congenial young lady who delights in meddling in other people’s affairs. She is perpetually trying to unite men and women who are utterly wrong for each other. Despite her interest in romance, Emma is clueless about her own feelings, and her relationship with gentle Mr. Knightly.\",\n            \"popularity\":15.892,\n            \"poster_path\":\"/k1RXXkdEs8hDEgeLXPgCUebzs8n.jpg\",\n            \"release_date\":\"1996-08-02\",\n            \"title\":\"Emma\",\n            \"video\":false,\n            \"vote_average\":6.752,\n            \"vote_count\":503\n         }\n      ],\n      \"total_pages\":500,\n      \"total_results\":10000\n   }\n}".data(using: .utf8)!)
        
        movie.movieRankings = exampleMovieOMDBFull
        
        return movie
    }
    
    static let exampleMovieOMDBFull = try! JSONDecoder().decode(MovieOMDB.self, from: "{\n   \"Title\":\"A Trip to the Moon\",\n   \"Year\":\"1902\",\n   \"Rated\":\"TV-G\",\n   \"Released\":\"04 Oct 1902\",\n   \"Runtime\":\"1 min\",\n   \"Genre\":\"Short, Action, Adventure\",\n   \"Director\":\"Georges Méliès\",\n   \"Writer\":\"Georges Méliès, Jules Verne, H.G. Wells\",\n   \"Actors\":\"Georges Méliès, Victor André, Bleuette Bernon\",\n   \"Plot\":\"A group of astronomers go on an expedition to the Moon.\",\n   \"Language\":\"French, English\",\n   \"Country\":\"France\",\n   \"Awards\":\"1 win\",\n   \"Poster\":\"https://m.media-amazon.com/images/M/MV5BMzcyYzhlMzctYjg1NS00ODJlLTkxOTUtYmQ4YjgxMWY5MGFlXkEyXkFqcGdeQXVyNDE5MTU2MDE@._V1_SX300.jpg\",\n   \"Ratings\":[\n      {\n         \"Source\":\"Internet Movie Database\",\n         \"Value\":\"8.2/10\"\n      },\n      {\n         \"Source\":\"Rotten Tomatoes\",\n         \"Value\":\"100%\"\n      }\n   ],\n   \"Metascore\":\"N/A\",\n   \"imdbRating\":\"8.2\",\n   \"imdbVotes\":\"49,498\",\n   \"imdbID\":\"tt0000417\",\n   \"Type\":\"movie\",\n   \"tomatoMeter\":\"N/A\",\n   \"tomatoImage\":\"N/A\",\n   \"tomatoRating\":\"N/A\",\n   \"tomatoReviews\":\"N/A\",\n   \"tomatoFresh\":\"N/A\",\n   \"tomatoRotten\":\"N/A\",\n   \"tomatoConsensus\":\"N/A\",\n   \"tomatoUserMeter\":\"N/A\",\n   \"tomatoUserRating\":\"N/A\",\n   \"tomatoUserReviews\":\"N/A\",\n   \"tomatoURL\":\"https://www.rottentomatoes.com/m/le_voyage_dans_la_lune\",\n   \"DVD\":\"11 Mar 2008\",\n   \"BoxOffice\":\"N/A\",\n   \"Production\":\"N/A\",\n   \"Website\":\"N/A\",\n   \"Response\":\"True\"\n}".data(using: .utf8)!)
}
