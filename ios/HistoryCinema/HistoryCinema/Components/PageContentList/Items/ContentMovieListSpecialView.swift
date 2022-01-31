//
//  ContentMovieSpecialView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/03/21.
//

import SwiftUI

struct ContentMovieListSpecialView: View {
    let contentMovieListSpecial: ContentMovieListSpecial
    @State private var currentPage = 0
    
    var body: some View {
        VStack {
            ZStack(alignment: .bottomTrailing) {
                let pages = contentMovieListSpecial.movies?.map {
                    MovieListItemSpecialView(movie: $0)
                } ?? []
                
                PageViewController(pages: pages, currentPage: $currentPage)
                    .background(Color.white)
                PageControl(
                    numberOfPages: pages.count,
                    currentPage: $currentPage,
                    pageIndicatorTintColor: UIColor(colorName: "md_black_1000")
                )
                    .padding(.trailing)
            }
            .aspectRatio(1, contentMode: .fit)
            .background(Color.white)
            .listRowInsets(EdgeInsets())
            
            ContentInformationView(contentInformation: contentMovieListSpecial.information)
        }
        .background(Color.white)
        .fixedSize(horizontal: false, vertical: true)
    }
}

struct ContentMovieListSpecialView_Previews: PreviewProvider {
    static var previews: some View {
        ContentMovieListSpecialView(contentMovieListSpecial: Content.exampleMovieSpecial)
    }
}
