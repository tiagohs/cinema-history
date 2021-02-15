//
//  HistoryCinemaApp.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 13/02/21.
//

import SwiftUI

@main
struct HistoryCinemaApp: App {
    @StateObject private var homePresenter = HomePresenter(movieService: MovieService())
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(homePresenter)
        }
    }
}
