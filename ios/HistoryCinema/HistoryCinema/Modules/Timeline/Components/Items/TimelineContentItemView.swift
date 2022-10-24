//
//  TimelineContentItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import SwiftUI

struct TimelineContentItemView: View {
    
    let timelinePage: TimelinePage!
    let timelineItem: TimelineItem!
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    
    @Environment(\.colorScheme) var colorScheme
    
    var body: some View {
        let marginStartVerticalLine = 70
        let marginStartContentRight = marginStartVerticalLine + 20
        let leftContentWidth = marginStartVerticalLine + 10
        let marginStartVerticalLineMain = marginStartVerticalLine + 9
        let year = timelineItem.year?.split(separator: "\n")
        
        VStack {
            ZStack(alignment: .topLeading) {
                Rectangle()
                    .fill(.black)
                    .frame(width: 3)
                    .padding(.leading, CGFloat(marginStartVerticalLineMain))
                
                VStack(alignment: .leading) {
                    ZStack(alignment: .bottomLeading) {
                        VStack(alignment: .trailing) {
                            // Left
                            Text(year?[0] ?? "")
                                .font(.oswaldBold(size: 38))
                                .multilineTextAlignment(.leading)
                                .foregroundColor(Color.textPrimary)
                                .frame(height: 38)
                            Text(year?[1] ?? "")
                                .font(.oswaldBold(size: 38))
                                .multilineTextAlignment(.leading)
                                .foregroundColor(Color.textPrimary)
                                .frame(height: 30)
                                
                        }
                        .frame(width: CGFloat(leftContentWidth), alignment: .trailing)
                        .padding(.bottom, 30)
                        
                        VStack(alignment: .trailing) {
                            // Right
                            ZStack(alignment: .bottomTrailing) {
                                
                                
                                CustomImage(
                                    image: timelineItem.image,
                                    imageType: .local,
                                    placeholderType: .movie,
                                    width: 200,
                                    height: 150
                                )
                                .frame(width: 200, height: 150)
                                .padding(.bottom, 10)
                                
                                Text(timelineItem.title ?? "")
                                    .font(.oswaldBold(size: 18))
                                    .textCase(.uppercase)
                                    .multilineTextAlignment(.center)
                                    .foregroundColor(Color(UIColor(colorName: timelinePage.titleTextColor!)))
                                    .padding(.horizontal)
                                    .padding(.vertical, 5)
                                    .background(Color(UIColor(colorName: timelinePage.color!)))
                                    .frame(maxWidth: 200)
                                    .padding(.leading, 10)
                                    .padding(.bottom, 50)
                                    .padding(.trailing, 150)
                            }
                            .frame(maxWidth: .infinity, alignment: .bottomTrailing)
                        }
                        .padding(.top, 50)
                        .padding(.leading, CGFloat(marginStartContentRight))
                        
                        Rectangle()
                            .fill(.black)
                            .frame(height: 3)
                            .padding(.bottom, 9)
                            .padding(.leading, CGFloat(marginStartVerticalLine))
                        
                        Circle()
                            .strokeBorder(Color.black, lineWidth: 3)
                            .background(Circle().fill(.white))
                            .frame(width: 20, height: 20)
                            .padding(.leading, CGFloat(marginStartVerticalLine))
                    }
                    
                    VStack {
                        // Bottom
                        if let description = timelineItem.description?.replacingOccurrences(of: "<[^>]+>", with: "", options: .regularExpression, range: nil) {
                            Text(description)
                                .font(.proximaNovaRegular(size: 14))
                                .multilineTextAlignment(.leading)
                                .foregroundColor(.textPrimary)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                    .padding(.leading, CGFloat(marginStartContentRight))
                    .padding(.trailing, 150)
                }
                .padding(.top, 25)
                .padding(.bottom, 80)
            }
        }
    }
}

struct TimelineContentItemView_Previews: PreviewProvider {
    static var previews: some View {
        ScrollView {
            TimelineContentItemView(
                timelinePage: TimelinePage.exampleTimelinePage,
                timelineItem: Timeline.exampleItem
            ) { textViewLinkScreen in
                
            }
        }
    }
}
