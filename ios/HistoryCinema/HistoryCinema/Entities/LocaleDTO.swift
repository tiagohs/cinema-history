//
//  LocaleDTO.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 13/02/21.
//

import Foundation

import Foundation

class LocaleDTO {
    var id: String
    var isoCountry: String
    var displayName: String
    
    init(_ id: String, _ isoCountry: String, _ displayName: String) {
        self.id = id
        self.isoCountry = isoCountry
        self.displayName = displayName
    }
}
