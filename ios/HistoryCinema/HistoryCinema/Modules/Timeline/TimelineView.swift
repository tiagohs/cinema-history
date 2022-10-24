//
//  TimelineView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import SwiftUI

struct TimelineView: View {
    
    @ObservedObject private var presenter: TimelinePresenter = TimelineWireframe.buildPresenter()
    
    let timelineId: Int!
    
    @State private var pageLinkModel: PageLinkModel? = nil
    
    var body: some View {
        ScrollView {
            LazyVStack {
                if presenter.isLoading {
                    ProgressView()
                        .padding()
                }
                
                if let timelineList = self.presenter.timelinePage?.timelineList, !timelineList.isEmpty {
                    ForEach(0 ..< timelineList.count) { index in
                        let timeline = timelineList[index]
                        
                        TimelineContentView(timelinePage: presenter.timelinePage, timeline: timeline) { textViewLinkScreen in
                            self.pageLinkModel = PageLinkModel(textViewLinkScreen?.id, textViewLinkScreen?.screenType)
                        }
                    }
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
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema! Por favor, tente novamente."),
                  dismissButton: .default(Text("Tentar novamente")) {
                      presenter.fetchTimelinePage(timelineId)
                  }
            )
        })
        .onAppear {
            presenter.viewAppears()
            presenter.fetchTimelinePage(timelineId)
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct TimelineView_Previews: PreviewProvider {
    static var previews: some View {
        TimelineView(timelineId: 1)
    }
}
