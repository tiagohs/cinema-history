//
//  TimelineContentTitleView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import SwiftUI

struct TimelineContentTitleView: View {
    
    let timelinePage: TimelinePage!
    let timelineTitle: TimelineTitle!
    
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        let marginStartVerticalLine = 70
        let marginStartContentRight = marginStartVerticalLine + 20
        let marginStartVerticalLineMain = marginStartVerticalLine + 9
        
        VStack(alignment: .leading) {
            TimelineContentTitleNavigationView(
                timelinePage: timelinePage,
                nextTitle: timelineTitle.next ?? "",
                previousTitle: timelineTitle.previous ?? ""
            )
            
            ZStack(alignment: .topLeading) {
                Rectangle()
                    .fill(.black)
                    .frame(height: 3)
                    .padding(.top, 9)
                
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
                    Image(systemName: "chevron.down")
                        .font(.system(size: 22, weight: .bold))
                        .padding(.trailing)
                        .foregroundColor(Color.textPrimary)
                    
                    Rectangle()
                        .fill(Color(UIColor(colorName: timelinePage.color!)))
                        .frame(width: 50, height: 5)
                        .padding(.top, 50)
                    
                    StyledTextView(
                        content: timelineTitle.title ?? "",
                        fontName: "Oswald-Bold",
                        size: 28,
                        color: Color.textPrimaryHEX(colorScheme)
                    )
                        .fixedSize(horizontal: false, vertical: true)
                }
                .padding(.top, 25)
                .padding(.bottom, 80)
                .padding(.leading, CGFloat(marginStartContentRight))
            }
        }
    }
}

struct TimelineContentTitleView_Previews: PreviewProvider {
    static var previews: some View {
        
        Group {
            TimelineContentTitleView(
                timelinePage: TimelinePage.exampleTimelinePage,
                timelineTitle: Timeline.exampleTitle
            )
            TimelineContentTitleView(
                timelinePage: TimelinePage.exampleTimelinePage2,
                timelineTitle: Timeline.exampleTitle2
            )
        }
    }
}
