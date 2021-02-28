//
//  MainTopicListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI
import UIKit

struct MainTopicListView<Content : View>: View {
    let mainTopicList: [MainTopic]
    
    @ViewBuilder var MainTopicItemDestination: (_ mainTopic: MainTopicItem) -> Content
    
    var body: some View {
        List {
            ForEach(0 ..< mainTopicList.count) { index in
                let mainTopic = mainTopicList[index]
                
                if mainTopic.layoutType == .quote {
                    MainTopicQuoteView(quoteMainTopic: mainTopic as! QuoteMainTopic)
                } else {
                    switch mainTopic.mainTopicType {
                    case .awards:
                        MainTopicAwardsView(awardMainTopic: mainTopic as! AwardMainTopic)
                    case .directors:
                        MainTopicDirectorsView(directorsMainTopic: mainTopic as! DirectorsMainTopic)
                    case .history_cinema:
                        let mainTopicItem = mainTopic as! MainTopicItem
                        
                        NavigationLink(destination: MainTopicItemDestination(mainTopicItem)) {
                            if (mainTopicItem.layoutType != .full) {
                                MainTopicHistoryCinemaView(mainTopicItem: mainTopicItem)
                                    .padding()
                                    .listRowInsets(EdgeInsets())
                                    .transition(.slide)
                            } else {
                                MainTopicHistoryCinemaView(mainTopicItem: mainTopicItem)
                                    .listRowInsets(EdgeInsets())
                                    .transition(.slide)
                            }
                        }
                        
                    case .mil_movies:
                        MainTopicMilMoviesView(milMoviesMainTopic: mainTopic as! MilMoviesMainTopic)
                    default:
                        AnyView(EmptyView())
                    }
                }
            }
        }
        .id(UUID())
    }
}

struct MainTopicListView_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopicList = [
            MainTopic.example(MainTopicsType.history_cinema),
            MainTopic.example(MainTopicsType.awards),
            MainTopic.example(MainTopicsType.directors),
            MainTopic.example(MainTopicsType.mil_movies)
        ]
        
        MainTopicListView(mainTopicList: mainTopicList) { _ in
            AnyView(EmptyView())
        }
            .background(Color.black)
    }
}
