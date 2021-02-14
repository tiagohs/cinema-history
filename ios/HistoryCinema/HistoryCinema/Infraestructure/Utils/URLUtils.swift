//
//  URLUtils.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 13/02/21.
//

import Foundation

class URLUtils {
    
    static func formatIMDBUrl(imdbId: String?) -> String? {
        guard let id = imdbId else { return nil }
        
        return "\(Constants.URL.IMDB_URL)title/\(id)"
    }
    
    static func formatWikiUrl(wikiSearchTerm: String?) -> String? {
        guard let search = wikiSearchTerm else { return nil }
        
        return "\(Constants.URL.WIKI_URL)index.php?search=\(search)"
    }
    
    static func formartPersonIMDB(imdbId: String?) -> String? {
        guard let id = imdbId else { return nil }
        
        return "\(Constants.URL.IMDB_URL)name/\(id)"
    }
    
    static func formatYoutubeUrl(videoId: String?) -> String? {
        guard let id = videoId else { return nil }
        
        return "https://i.ytimg.com/vi/\(id)/hqdefault.jpg"
    }
    
    static func formatTwitterUrl(twitterId: String?) -> String? {
        guard let id = twitterId else { return nil }
        
        return "twitter://user?screen_name=\(id)"
    }
    
    static func formatFacebookUrl(facebookId: String?) -> String? {
        guard let id = facebookId else { return nil }
        
        return "https://www.facebook.com/\(id)"
    }
    
    static func formatInstagramUrl(instagramId: String?) -> String? {
        guard let id = instagramId else { return nil }
        
        return "https://instagram.com/_u/\(id)"
    }
}
