//
//  MainTopicListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicListView: View {
    let mainTopicList: [MainTopic]
    
    var body: some View {
        NavigationView {
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
                            MainTopicHistoryCinemaView(mainTopicItem: mainTopic as! MainTopicItem)
                        case .mil_movies:
                            MainTopicMilMoviesView(milMoviesMainTopic: mainTopic as! MilMoviesMainTopic)
                        default:
                            Text("")
                        }
                    }
                }
            }
        }
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
        
        MainTopicListView(mainTopicList: mainTopicList)
    }
}
