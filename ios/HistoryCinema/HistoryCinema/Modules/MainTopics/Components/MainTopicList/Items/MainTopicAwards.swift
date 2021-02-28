//
//  MainTopicAwards.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicAwards: View {
    let awardMainTopic: AwardMainTopic
    
    var body: some View {
        VStack {
            
        }
    }
}

struct MainTopicAwards_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.awards)
        
        MainTopicAwards(awardMainTopic: mainTopic as! AwardMainTopic)
    }
}
