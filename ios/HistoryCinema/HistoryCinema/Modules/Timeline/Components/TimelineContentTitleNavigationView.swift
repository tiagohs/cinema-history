//
//  TimelineContentTitleNavigationView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import SwiftUI

struct TimelineContentTitleNavigationView: View {
    
    let timelinePage: TimelinePage!
    let nextTitle: String!
    let previousTitle: String!
    
    var body: some View {
        HStack {
            if let timelineId = timelinePage.id {
                if (timelineId > 1) {
                    Button(action: {
                        
                    }) {
                        HStack {
                            Image(systemName: "chevron.left")
                                .padding(.leading)
                                .foregroundColor(Color.textPrimary)
                            
                            Text(previousTitle)
                                .font(.oswaldBold(size: 14))
                                .multilineTextAlignment(.leading)
                                .foregroundColor(Color.textPrimary)
                        }
                    }
                }
                
                Spacer()
                
                if timelineId < 7 {
                    Button(action: {
                        
                    }) {
                        HStack {
                            Text(nextTitle)
                                .font(.oswaldBold(size: 14))
                                .multilineTextAlignment(.leading)
                                .foregroundColor(Color.textPrimary)
                            
                            Image(systemName: "chevron.right")
                                .padding(.trailing)
                                .foregroundColor(Color.textPrimary)
                        }
                    }
                }
            }
        }
    }
}

struct TimelineContentTitleNavigationView_Previews: PreviewProvider {
    static var previews: some View {
        TimelineContentTitleNavigationView(
            timelinePage: TimelinePage.exampleTimelinePage,
            nextTitle: "1939 a 1959",
            previousTitle: "1930 a 1939"
        )
    }
}
