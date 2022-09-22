//
//  HistoryToolbar.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 31/01/22.
//

import SwiftUI

struct HistoryToolbar: View {
    let mainTopic: MainTopicItem
    
    var body: some View {
        ZStack {
            Text(mainTopic.title)
                .textCase(.uppercase)
                .font(.oswaldBold(size: 14))
                .multilineTextAlignment(.center)
                .foregroundColor(Color.white)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
        }
        .background((mainTopic.color != nil) ? Color(UIColor(colorName: mainTopic.color!)) : Color.black)
    }
}

struct HistoryToolbar_Previews: PreviewProvider {
    static var previews: some View {
        HistoryToolbar(mainTopic: MainTopicItem.example(.history_cinema) as! MainTopicItem)
    }
}
