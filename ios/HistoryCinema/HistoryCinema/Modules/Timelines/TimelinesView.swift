//
//  TimelinesView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import SwiftUI

struct TimelinesView: View {
    
    @ObservedObject private var presenter: TimelinesPresenter = TimelinesWireframe.buildPresenter()
    
    @State private var currentPage = 0
    
    var body: some View {
        let indexes = [1, 2, 3, 4, 5, 6, 7]
        let pages = indexes.map {
            TimelineView(timelineId: $0)
        }
        
        VStack {
            PageViewController(pages: pages, currentPage: $currentPage)
            
        }
        .navigationBarTitle("")
        .navigationBarHidden(true)
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema!"),
                  dismissButton: .default(Text("Ok")) {
                    
                  }
            )
        })
        .onAppear {
            presenter.viewAppears()
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct TimelinesView_Previews: PreviewProvider {
    static var previews: some View {
        TimelinesView()
    }
}
