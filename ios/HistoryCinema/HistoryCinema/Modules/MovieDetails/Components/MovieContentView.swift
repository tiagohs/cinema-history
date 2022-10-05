//
//  MovieContentView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/09/22.
//

import SwiftUI

struct MovieContentView: View {
    let movie: Movie!
    let movieExtra: MovieExtraInfo?
    
    var body: some View {
        let cast = movie.credits?.cast?.map({ cast in
            return PersonItem(id: cast.id, name: cast.name, subtitle: cast.character, pictureId: cast.profilePath)
        }) ?? []
        let crew = movie.credits?.crew?.map({ crew in
            return PersonItem(id: crew.id, name: crew.name, subtitle: crew.department, pictureId: crew.profilePath)
        }) ?? []
        
        ZStack(alignment: .top) {
            MovideDetailsHeaderView(movie: movie)
            
            VStack {
                MovieContentInfoView(movie: movie)
                    .padding(.top, 340)
                
                MovieContentSummaryView(movie: movie)
                    .padding(.vertical, 10)
                
                MovieContentWatchOn(movieExtraInfo: movieExtra)
                    .padding(.vertical, 10)
                
                MovieContentPersonListView(sectionName: "Elenco", personList: cast)
                    .padding(.vertical, 10)
                
                MovieContentPersonListView(sectionName: "Equipe Técnica", personList: crew)
                    .padding(.vertical, 10)
                
                MovieContentMidiaView(movie: movie)
                    .padding(.vertical, 10)
            }
            .padding(.bottom, 10)
        }
    }
}

struct MovieContentView_Previews: PreviewProvider {
    static var previews: some View {
        ZStack(alignment: .topLeading) {
            ScrollView {
                LazyVStack {
                    MovieContentView(
                        movie: Movie.exampleMovieFull(),
                        movieExtra: Movie.exampleMovieExtraInfo
                    )
                }
            }
            .edgesIgnoringSafeArea(.all)
        }
    }
}
