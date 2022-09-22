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
                        self.presenter.presentSummary(mainTopic)
                    }
                )
            }
        }
        .toolbar {
            ToolbarItem(placement: .principal) {
                HStack(alignment: .center) {
                    Text("A História do")
                        .font(.oswaldBold(size: 18))
                        .foregroundColor(Color.textPrimary)
                    
                    Text("Cinema")
                        .font(.billionaireMediumGrunge(size: 38))
                        .foregroundColor(Color.textPrimary)
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
    }
}
