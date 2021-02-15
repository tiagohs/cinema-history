//
//  ImageType.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation

enum ImageType: String, Codable {
    case local, online
    case onlineFirebase = "online_firebase"
}
