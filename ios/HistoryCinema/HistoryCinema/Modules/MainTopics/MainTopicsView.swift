//
//  MainTopicsView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicsView: View {
    
    @ObservedObject private var presenter: MainTopicsPresenter = MainTopicsWireframe.buildPresenter()
    
    let mainTopicType: MainTopicsType
    
    var body: some View {
        VStack {
            if (presenter.mainTopics.count == 0) {
                ProgressView()
            }
            
            if !presenter.mainTopics.isEmpty {
                MainTopicListView(
                    mainTopicList: presenter.mainTopics,
                    MainTopicItemDestination: { mainTopic in
                        switch mainTopicType {
                            case .history_cinema:
                                self.presenter.presentSummary(mainTopic as! MainTopicItem)
                            case .mil_movies:
                            self.presenter.presentMilMovies(mainTopic as! MilMoviesMainTopic)
                            default:
                                AnyView(EmptyView())
                        }
                    }
                )
            }
        }
        .toolbar {
            ToolbarItem(placement: .principal) {
                switch mainTopicType {
                    case .history_cinema:
                        HStack(alignment: .center) {
                            Text("A História do")
                                .font(.oswaldBold(size: 18))
                                .foregroundColor(Color.textPrimary)
                            
                            Text("Cinema")
                                .font(.billionaireMediumGrunge(size: 38))
                                .foregroundColor(Color.textPrimary)
                        }
                    case .mil_movies:
                        Text("1001 Filmes")
                            .font(.bigshouldersDisplayBold(size: 18))
                            .foregroundColor(Color.textPrimary)
                    default:
                        AnyView(EmptyView())
                }
                
            }
        }
        .foregroundColor(Color.textPrimary)
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema! Por favor, tente novamente."),
                  dismissButton: .default(Text("Tentar novamente")) { presenter.fetchMainTopicsBy(mainTopicType) }
            )
        })
        .onAppear {
            presenter.viewAppears()
            presenter.fetchMainTopicsBy(mainTopicType)
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct MainTopicsView_Previews: PreviewProvider {
    static var previews: some View {
        MainTopicsView(mainTopicType: MainTopicsType.history_cinema)
        MainTopicsView(mainTopicType: MainTopicsType.mil_movies)
    }
}
