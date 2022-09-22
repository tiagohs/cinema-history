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
    
    init(mainTopic: MainTopicItem, startIndex: Int, summaryList: [SummaryModel]) {
        currentPage = startIndex
        self.mainTopic = mainTopic
        self.summaryList = summaryList
    }
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            ZStack(alignment: .bottomTrailing) {
                let pages = summaryList.map {
                    HistoryPageView(
                        mainTopic: mainTopic,
                        summary: $0
                    )
                } 
                
                VStack {
                    PageViewController(pages: pages, currentPage: $currentPage)
                    
                    VStack {
                        HStack {
                            Button(action: {
                                
                            }) {
                                Image(systemName: "square.and.arrow.up")
                                    .padding()
                                    .foregroundColor(Color.bottomHistoryNavigationTextColor)
                            }
                            
                            Spacer()
                            
                            Button(action: {
                                if (currentPage > 0) {
                                    currentPage = currentPage - 1
                                }
                            }) {
                                Image(systemName: "arrow.left")
                                    .padding()
                                    .foregroundColor(Color.bottomHistoryNavigationTextColor)
                            }
                            Button(action: {
                                if (currentPage < (summaryList.count - 1)) {
                                    currentPage = currentPage + 1
                                }
                            }) {
                                Image(systemName: "arrow.right")
                                    .padding()
                                    .foregroundColor(Color.bottomHistoryNavigationTextColor)
                            }
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .background(Color.bottomHistoryNavigationBackgroundColor)
                }
                
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbarBackground(Color.black, for: .navigationBar, .tabBar)
        .toolbarBackground(.visible, for: .navigationBar)
        .foregroundColor(Color.textPrimary)
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
            startIndex: 0,
            summaryList: summaryList
        )
    }
}
