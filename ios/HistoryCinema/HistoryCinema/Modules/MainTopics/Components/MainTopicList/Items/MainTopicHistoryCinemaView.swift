//
//  MainTopicHistoryCinema.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicHistoryCinemaView: View {
    let mainTopicItem: MainTopicItem
    
    var body: some View {
        MainTopicView(
            subtitle: mainTopicItem.subtitle,
            title: mainTopicItem.title,
            description: mainTopicItem.description,
            image: mainTopicItem.image,
            layoutType: mainTopicItem.layoutType
        )
    }
}

struct MainTopicHistoryCinema_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.history_cinema)
        
        MainTopicHistoryCinemaView(mainTopicItem: mainTopic as! MainTopicItem)
    }
}
