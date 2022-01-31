//
//  SummaryHeader.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 15/03/21.
//

import SwiftUI

struct SummaryHeader: View {
    let mainTopic: MainTopicItem
    
    var body: some View {
        Section.init {
            GeometryReader { geometry in
                ZStack(alignment: .bottomTrailing) {
                    if geometry.frame(in: .global).minY <= 0 {
                        Image((mainTopic.presentationImage?.url!)!)
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                                .frame(width: geometry.size.width, height: geometry.size.height)
                                .offset(y: geometry.frame(in: .global).minY/9)
                                .clipped()
                                .shadow(color: .black, radius: 5)
                        
                        SummaryQuoteView(quote: mainTopic.quote)
                            .offset(y: geometry.frame(in: .global).minY/2)
                    } else {
                        Image((mainTopic.presentationImage?.url!)!)
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                                .frame(width: geometry.size.width, height: geometry.size.height + geometry.frame(in: .global).minY)
                                .clipped()
                                .shadow(color: .black, radius: 5)
                                .offset(y: -geometry.frame(in: .global).minY)
                        
                        SummaryQuoteView(quote: mainTopic.quote)
                            .offset(y: -geometry.frame(in: .global).minY)
                    }
                }
            }
            .frame(height: 600)
        }
    }
}

struct SummaryHeader_Previews: PreviewProvider {
    static var previews: some View {
        SummaryHeader(mainTopic: MainTopic.example(.history_cinema) as! MainTopicItem)
    }
}
