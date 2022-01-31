//
//  HistoryCinemaApp.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 13/02/21.
//

import SwiftUI

@main
struct HistoryCinemaApp: App {
    
    init() {
        let navigationAppearance = UINavigationBarAppearance()
        
        navigationAppearance.configureWithOpaqueBackground()
        navigationAppearance.backgroundColor = .clear
        
        navigationAppearance.titleTextAttributes = [.foregroundColor: UIColor.white]
        navigationAppearance.largeTitleTextAttributes = [.foregroundColor: UIColor.white]
       
        UINavigationBar.appearance().standardAppearance = navigationAppearance
        UINavigationBar.appearance().compactAppearance = navigationAppearance
        UINavigationBar.appearance().scrollEdgeAppearance = navigationAppearance

        UINavigationBar.appearance().tintColor = .white
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.colorScheme, .dark)
        }
    }
}
