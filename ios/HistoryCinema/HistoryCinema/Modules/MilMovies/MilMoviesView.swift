//
//  MilMoviesView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 26/09/22.
//

import SwiftUI

struct MilMoviesView: View {
    
    @ObservedObject private var presenter: MilMoviesPresenter = MilMoviesWireframe.buildPresenter()
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    let mainTopic: MilMoviesMainTopic
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            ScrollView {
                VStack {
                    MilMoviesHeader(mainTopic: mainTopic)
                    
                    if (!presenter.movieList.isEmpty) {
                        MilMoviesList(movieList: presenter.movieList) {
                            presenter.fetchMoviesBy(mainTopicItem: mainTopic)
                        } MovieDetailsDestination: { movie in
                            AnyView(EmptyView())
                        }
                    }
                    
                    if (presenter.movieList.count == 0 || presenter.isLoading) {
                        ProgressView()
                    }
                }
            }
            .edgesIgnoringSafeArea(.all)
            
            Button(action: {
                self.presentationMode.wrappedValue.dismiss()
            }) {
                Image(systemName: "chevron.left")
                    .font(.system(size: 16, weight: .bold))
                    .padding()
                    .background(Color.backgroundInverse)
                    .foregroundColor(Color.textInverse)
                    .clipShape(Circle())
            }
            .shadow(color: Color.backgroundInverse, radius: 5)
            .padding()
        }
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
