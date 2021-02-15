//
//  LocalContentService.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

class LocalContentService {
    @Published var homeContent: [HomeContentItem] = load("homecontent.json")
}

func load<T: Decodable>(_ filename: String) -> T {
    let data: Data
    
    guard let file = Bundle.main.url(forResource: filename, withExtension: nil) else {
        fatalError("Could't find \(filename) in main bundle")
    }
    
    do {
        data = try (Data(contentsOf: file))
    } catch {
        fatalError("Could't find \(filename) in main : \n\(error)")
    }
    
    do {
        let decoder = JSONDecoder()
        
        return try decoder.decode(T.self, from: data)
    } catch {
        fatalError("Could't find \(filename) in main : \n\(error)")
    }
}
