//
//  HistoryPagesView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct HistoryPagesView: View {
    
    @ObservedObject private var presenter: HistoryPagesPresenter = HistoryPagesWireframe.buildPresenter()
    
    @State private var currentPage = 0
    
    let mainTopic: MainTopicItem
    let summaryList: [SummaryModel]
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            ZStack(alignment: .bottomTrailing) {
                let pages = summaryList.map {
                    HistoryPageView(
                        mainTopic: mainTopic,
                        summary: $0
                    )
                } 
                
                PageViewController(pages: pages, currentPage: $currentPage)
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) { 
                HistoryToolbar(mainTopic: mainTopic)
            }
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
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct HistoryPagesView_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopicItem = MainTopic.example(.history_cinema) as! MainTopicItem
        let summaryList = [
            SummaryModel.example,
            SummaryModel.example,
            SummaryModel.example
        ]
        
        HistoryPagesView(
            mainTopic: mainTopicItem,
            summaryList: summaryList
        )
    }
}
