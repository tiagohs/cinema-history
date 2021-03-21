//
//  ContentView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 13/02/21.
//

import SwiftUI
import Combine

struct ContentView: View {
    
    var body: some View {
        //HomeView()
        
        let mainTopicItem = MainTopic.example(.history_cinema) as! MainTopicItem
        let summary = SummaryModel.example
        
        HistoryPageItemView(mainTopic: mainTopicItem, summary: summary)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
