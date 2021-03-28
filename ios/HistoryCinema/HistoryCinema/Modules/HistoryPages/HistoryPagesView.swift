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
        let mainTopicItem = MainTopic.example(.history_cinema) as! MainTopicItem
        
        ZStack(alignment: .topLeading) {
            ZStack(alignment: .bottomTrailing) {
                let pages: [HistoryPageView] = summaryList.map {
                    HistoryPageView(
                        mainTopic: mainTopicItem,
                        summary: $0
                    )
                } 
                
                PageViewController(pages: pages, currentPage: $currentPage)
//                PageControl(
//                    numberOfPages: pages.count,
//                    currentPage: $currentPage,
//                    pageIndicatorTintColor: UIColor(colorName: "md_black_1000")
//                )
//                    .frame(width: CGFloat(pages.count * 18))
//                    .padding(.trailing)
            }
            .edgesIgnoringSafeArea(.all)
            .background(Color.white)
            .listRowInsets(EdgeInsets())
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
