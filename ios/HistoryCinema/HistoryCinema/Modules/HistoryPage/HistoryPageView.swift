//
//  HistoryPage.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/03/21.
//

import SwiftUI

struct HistoryPageView: View {
    
    @ObservedObject private var presenter: HistoryPagePresenter = HistoryPageWireframe.buildPresenter()
    
    let mainTopic: MainTopicItem
    let summary: SummaryModel
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    var onClickListLink: ((TextViewLinkScreenType, Int?) -> Void)? = nil
    
    var body: some View {
        VStack {
            if (presenter.historyPage == nil) {
                ProgressView()
                    
            } else {
                HistoryPageItemView(
                    mainTopic: mainTopic,
                    summary: summary,
                    page: presenter.historyPage!,
                    onClickLink: onClickLink,
                    onClickListLink: onClickListLink
                )
            }
        }
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema! Por favor, tente novamente."),
                  dismissButton: .default(Text("Tentar novamente")) {
                        presenter.fetchPageBy(mainTopic, summary)
                  }
            )
        })
        .onAppear {
            presenter.viewAppears()
            presenter.fetchPageBy(mainTopic, summary)
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct HistoryPageView_Previews: PreviewProvider {
    static var previews: some View {
        HistoryPageView(
            mainTopic: MainTopic.example(.history_cinema) as! MainTopicItem,
            summary: SummaryModel.example
        )
    }
}
