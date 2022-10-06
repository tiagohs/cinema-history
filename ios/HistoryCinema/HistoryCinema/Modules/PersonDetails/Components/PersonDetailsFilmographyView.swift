//
//  PersonDetailsFilmographyView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 05/10/22.
//

import SwiftUI

struct PersonDetailsFilmographyView: View {
    let person: Person!
    
    var onClickListLink: ((TextViewLinkScreenType, Int?) -> Void)? = nil

    var body: some View {
        let movies = person.filmography()
        
        if !movies.isEmpty {
            ContentContainer {
                VStack(alignment: .leading) {
                    Text("Filmografia")
                        .font(.oswaldRegular(size: 24))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 5)
                        .padding(.horizontal, 20)
                        .padding(.top, 20)
                    
                    MovieListView(movieList: movies, onClickListLink: onClickListLink)
                        .padding(.bottom, 20)
                }
            }
        } else {
            AnyView(EmptyView())
        }
    }
}

struct PersonDetailsFilmographyView_Previews: PreviewProvider {
    static var previews: some View {
        PersonDetailsFilmographyView(person: Person.examplePersonFull) {_,_ in
            
        }
    }
}
