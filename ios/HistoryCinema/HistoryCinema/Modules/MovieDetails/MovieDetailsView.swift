//
//  MovieDetailsView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/09/22.
//

import SwiftUI

struct MovieDetailsView: View {
    
    let movieId: Int!
    
    @ObservedObject private var presenter: MovieDetailsPresenter = MovieDetailsWireframe.buildPresenter()
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            ScrollView {
                VStack {
                    if presenter.isLoading {
                        ProgressView()
                            .padding()
                    }
                    
                    if !presenter.isLoading {
                        MovieContentView(
                            movie: presenter.movie,
                            movieExtra: presenter.movieExtra
                        )
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .edgesIgnoringSafeArea(.all)
            
            Button(action: {
                self.presentationMode.wrappedValue.dismiss()
            }) {
                Image(systemName: "xmark")
                    .font(.system(size: 16, weight: .bold))
                    .padding()
                    .background(Color.backgroundInverse)
                    .foregroundColor(Color.textInverse)
                    .clipShape(Circle())
            }
            .shadow(color: Color.backgroundInverse, radius: 5)
            .padding()
        }
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema! Por favor, tente novamente."),
                  dismissButton: .default(Text("Tentar novamente")) {
                    
                  }
            )
        })
        .onAppear {
            presenter.viewAppears()
            presenter.fetchMovieDetailsBy(movieId)
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct MovieDetailsView_Previews: PreviewProvider {
    static var previews: some View {
        MovieDetailsView(movieId: 775)
    }
}
