//
//  MainTopicDirectors.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicDirectorsView: View {
    let directorsMainTopic: DirectorsMainTopic
    
    var body: some View {
        VStack {
            
        }
    }
}

struct MainTopicDirectors_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.directors)
        
        MainTopicDirectorsView(directorsMainTopic: mainTopic as! DirectorsMainTopic)
    }
}
