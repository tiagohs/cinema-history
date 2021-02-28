//
//  MainTopicMilMovies.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicMilMovies: View {
    let milMoviesMainTopic: MilMoviesMainTopic
    
    var body: some View {
        VStack {
            
        }
    }
}

struct MainTopicMilMovies_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.mil_movies)
        
        MainTopicMilMovies(milMoviesMainTopic: mainTopic as! MilMoviesMainTopic)
    }
}
