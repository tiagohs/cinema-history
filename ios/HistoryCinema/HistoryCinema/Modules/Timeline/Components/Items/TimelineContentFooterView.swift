//
//  TimelineContentFooterView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import SwiftUI

struct TimelineContentFooterView: View {
    
    let timelinePage: TimelinePage!
    let timelineFooter: TimelineFooter!
    
    var body: some View {
        let marginStartVerticalLine = 70
        let marginStartContentRight = marginStartVerticalLine + 20
        let marginStartVerticalLineMain = marginStartVerticalLine + 9
        
        VStack(alignment: .leading) {
            ZStack(alignment: .bottomLeading) {
                Rectangle()
                    .fill(.black)
                    .frame(height: 3)
                    .padding(.bottom, 9)
                
                Rectangle()
                    .fill(.black)
                    .frame(width: 3)
                    .padding(.leading, CGFloat(marginStartVerticalLineMain))
                
                Circle()
                    .strokeBorder(Color.black, lineWidth: 3)
                    .background(Circle().fill(.white))
                    .frame(width: 20, height: 20)
                    .padding(.leading, CGFloat(marginStartVerticalLine))
                
                VStack(alignment: .leading) {
                    Image(systemName: "chevron.up")
                        .font(.system(size: 22, weight: .bold))
                        .padding(.trailing)
                        .padding(.top, 80)
                        .foregroundColor(Color.textPrimary)
                }
                .padding(.bottom, 25)
                .padding(.top, 80)
                .padding(.leading, CGFloat(marginStartContentRight))
            }
            
            TimelineContentTitleNavigationView(
                timelinePage: timelinePage,
                nextTitle: timelineFooter.next ?? "",
                previousTitle: timelineFooter.previous ?? ""
            )
        }
    }
}

struct TimelineContentFooterView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            TimelineContentFooterView(
                timelinePage: TimelinePage.exampleTimelinePage,
                timelineFooter: Timeline.exampleFooter
            )
            TimelineContentFooterView(
                timelinePage: TimelinePage.exampleTimelinePage2,
                timelineFooter: Timeline.exampleFooter2
            )
        }
    }
}
