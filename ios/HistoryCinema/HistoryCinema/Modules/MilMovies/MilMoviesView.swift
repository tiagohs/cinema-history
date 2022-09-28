//
//  MilMoviesView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 26/09/22.
//

import SwiftUI

struct MilMoviesView: View {
    
    let mainTopic: MilMoviesMainTopic
    
    @ObservedObject private var presenter: MilMoviesPresenter = MilMoviesWireframe.buildPresenter()
    
//    @Environment(\.presentationMode) var navPresentationMode: Binding<PresentationMode>
    
    @State private var showingMovieDetailsSheet = false
    
    @State private var pageLinkModel: PageLinkModel? = nil
    
    var body: some View {
        NavigationView {
            ZStack(alignment: .topLeading) {
                ScrollView {
                    LazyVStack {
                        MilMoviesHeader(mainTopic: mainTopic)
                        
                        if (!presenter.movieList.isEmpty) {
                            MilMoviesList(movieList: presenter.movieList) {
                                presenter.fetchMoviesBy(mainTopicItem: mainTopic)
                            } onItemClicked: { movie in
                                pageLinkModel = PageLinkModel(movie.id, nil)
                            } MovieDetailsDestination: { movie in
                                MovieDetailsView(movieId: movie.id)
                            }
                        }
                        
                        if (presenter.movieList.count == 0 || presenter.isLoading) {
                            ProgressView()
                        }
                    }
                }
                .edgesIgnoringSafeArea(.all)
                
    //            Button(action: {
    //                self.navPresentationMode.wrappedValue.dismiss()
    //            }) {
    //                Image(systemName: "chevron.left")
    //                    .font(.system(size: 16, weight: .bold))
    //                    .padding()
    //                    .background(Color.backgroundInverse)
    //                    .foregroundColor(Color.textInverse)
    //                    .clipShape(Circle())
    //            }
    //            .shadow(color: Color.backgroundInverse, radius: 5)
    //            .padding()
            }
        }
        .navigationTitle("")
        .sheet(item: $pageLinkModel) { item in
            MovieDetailsView(movieId: item.id)
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
            presenter.fetchMoviesBy(mainTopicItem: mainTopic)
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct MilMoviesView_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopicItem = MainTopic.example(.mil_movies) as! MilMoviesMainTopic
        
        MilMoviesView(mainTopic: mainTopicItem)
    }
}
