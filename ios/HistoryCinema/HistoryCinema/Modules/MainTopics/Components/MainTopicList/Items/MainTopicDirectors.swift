//
//  MainTopicDirectors.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicDirectors: View {
    let directorsMainTopic: DirectorsMainTopic
    
    var body: some View {
        VStack {
            
        }
    }
}

struct MainTopicDirectors_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.directors)
        
        MainTopicDirectors(directorsMainTopic: mainTopic as! DirectorsMainTopic)
    }
}
