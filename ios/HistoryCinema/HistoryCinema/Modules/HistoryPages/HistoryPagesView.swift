//
//  HistoryPagesView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct HistoryPagesView: View {
    
    @ObservedObject private var presenter: HistoryPagesPresenter
    
    @State private var currentPage = 0
    
    @State private var showingMovieDetailsSheet = false
    @State private var pageLinkModel: PageLinkModel? = nil
    
    let mainTopic: MainTopicItem
    let summaryList: [SummaryModel]
    
    init(mainTopic: MainTopicItem, startIndex: Int, summaryList: [SummaryModel]) {
        currentPage = startIndex
        self.mainTopic = mainTopic
        self.summaryList = summaryList
        
        presenter = HistoryPagesWireframe.buildPresenter()
    }
    
    private func onClick(type: TextViewLinkScreenType?, id: Int?) {
        self.pageLinkModel = PageLinkModel(id, type)
    }
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            ZStack(alignment: .bottomTrailing) {
                let pages = summaryList.map {
                    HistoryPageView(
                        mainTopic: mainTopic,
                        summary: $0,
                        onClickLink: { textViewLinkScreen in
                            onClick(type: textViewLinkScreen?.screenType, id: textViewLinkScreen?.id)
                        }) { type, id in
                            onClick(type: type, id: id)
                        }
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
        .sheet(item: self.$pageLinkModel) { item in
            if item.type == .movie {
                MovieDetailsView(movieId: item.id!)
            } else if item.type == .person {
                PersonDetailsView(personId: item.id)
            }
        }
        .navigationBarTitleDisplayMode(.inline)
//        .toolbarBackground(Color.black, for: .navigationBar, .tabBar)
//        .toolbarBackground(.visible, for: .navigationBar)
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
