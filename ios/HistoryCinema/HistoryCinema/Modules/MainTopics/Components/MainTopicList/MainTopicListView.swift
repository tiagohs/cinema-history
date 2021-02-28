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
        List {
            ForEach(0 ..< mainTopicList.count) { index in
                let mainTopic = mainTopicList[index]
                
                switch mainTopic.mainTopicType {
                case .awards:
                    MainTopicAwards(awardMainTopic: mainTopic as! AwardMainTopic)
                case .directors:
                    MainTopicDirectors(directorsMainTopic: mainTopic as! DirectorsMainTopic)
                case .history_cinema:
                    MainTopicHistoryCinema(mainTopicItem: mainTopic as! MainTopicItem)
                case .mil_movies:
                    MainTopicMilMovies(milMoviesMainTopic: mainTopic as! MilMoviesMainTopic)
                default:
                    Text("")
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
