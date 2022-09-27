//
//  ContentMovieSpecialView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/03/21.
//

import SwiftUI

struct ContentMovieListSpecialView: View {
    let contentMovieListSpecial: ContentMovieListSpecial
    
    @Environment(\.colorScheme) var colorScheme
    
    @State private var currentPage = 0
    
    var body: some View {
        VStack {
            VStack {
                let pages = contentMovieListSpecial.movies?.map {
                    MovieListItemSpecialView(movie: $0)
                } ?? []
                
                PageViewController(pages: pages, currentPage: $currentPage)
                PageControl(
                    numberOfPages: pages.count,
                    currentPage: $currentPage,
                    pageIndicatorTintColor: UIColor(hex: Color.textPrimaryHEX(colorScheme))
                )
            }
            .aspectRatio(1, contentMode: .fit)
            .listRowInsets(EdgeInsets())
            
            ContentInformationView(contentInformation: contentMovieListSpecial.information)
        }
        .fixedSize(horizontal: false, vertical: true)
    }
}

struct ContentMovieListSpecialView_Previews: PreviewProvider {
    static var previews: some View {
        ContentMovieListSpecialView(contentMovieListSpecial: Content.exampleMovieSpecial)
    }
}
