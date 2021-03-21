//
//  HistoryPagesView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct HistoryPagesView: View {
    
    @ObservedObject private var presenter: HistoryPagesPresenter = HistoryPagesWireframe.buildPresenter()
    
    let mainTopic: MainTopicItem
    
    var body: some View {
        ZStack {
            
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
        
        HistoryPagesView(mainTopic: mainTopicItem)
    }
}
