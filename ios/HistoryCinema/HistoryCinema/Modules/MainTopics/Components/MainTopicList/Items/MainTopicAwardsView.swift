//
//  MainTopicAwards.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicAwardsView: View {
    let awardMainTopic: AwardMainTopic
    
    var body: some View {
        VStack {
            
        }
    }
}

struct MainTopicAwards_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.awards)
        
        MainTopicAwardsView(awardMainTopic: mainTopic as! AwardMainTopic)
    }
}
