//
//  HomeView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import SwiftUI
import Combine

struct HomeView: View {
    
    @ObservedObject private var presenter: HomePresenter = HomeWireframe.buildPresenter() 
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading) {
                    HomeButtonListView(
                        ReferencesDestination: { self.presenter.presentReferences() },
                        GlossaryDestination: { self.presenter.presentGlossary() },
                        AboutDestination: { self.presenter.presentReferences() }
                    )
                    
                    if (presenter.homeContent.count == 0) {
                        ProgressView()
                    }
                    
                    if !presenter.homeContent.isEmpty {
                        HomeListView(
                            homeContentItemList: presenter.homeContent,
                            HomeItemDestination: { mainTopicType in
                                switch mainTopicType {
                                case .timeline:
                                    self.presenter.presentTimeline()
                                default:
                                    self.presenter.presentMainTopics(mainTopicType)
                                }
                            }
                        )
                    }
                }
            }
            .navigationBarTitle("Historia do cinema", displayMode: .automatic)
        }
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema! Por favor, tente novamente."),
                  dismissButton: .default(Text("Tentar novamente")) { presenter.fetchHomeContent() }
            )
        })
        .supportedOrientations(.portrait)
        .onAppear { presenter.viewAppears() }
        .onDisappear { presenter.viewDisappears() }
    }
}

#if DEBUG
    struct HomeView_Previews: PreviewProvider {
        static var previews: some View {
            HomeView()
        }
    }
#endif
