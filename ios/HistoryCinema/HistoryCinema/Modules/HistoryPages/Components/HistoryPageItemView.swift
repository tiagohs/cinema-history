//
//  HistoryPageItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct HistoryPageItemView: View {
    let mainTopic: MainTopicItem
    let summary: SummaryModel
    
    var body: some View {
        let mainTopicTimeBackgroundColor = mainTopic.color
        let imageHeight = summary.image?.imageStyle?.height
        let imageURL = summary.image?.url
        
        List {
            Section.init {
                VStack(alignment: .center) {
                    ZStack {
                        Text(mainTopic.title)
                            .textCase(.uppercase)
                            .font(.oswaldBold(size: 14))
                            .multilineTextAlignment(.center)
                            .foregroundColor(Color.white)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                    }
                    .background((mainTopicTimeBackgroundColor != nil) ? Color(UIColor(colorName: mainTopicTimeBackgroundColor!)) : Color.white)
                    
                    Text(summary.title)
                        .textCase(.uppercase)
                        .frame(maxWidth: .infinity, alignment: .center)
                        .font(.proximaNovaBold(size: 22))
                        .multilineTextAlignment(.center)
                        .foregroundColor(Color.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                    
                    Text(summary.description)
                        .frame(maxWidth: .infinity, alignment: .center)
                        .font(.proximaNovaRegular(size: 14))
                        .multilineTextAlignment(.center)
                        .foregroundColor(Color.white)
                        .padding(.horizontal, 16)
                        .padding(.bottom, 16)
                    
                    ZStack(alignment: Alignment(horizontal: .center, vertical: .top)) {
                        if (imageURL != nil) {
                            Image(imageURL!)
                                .resizable()
                                .frame(
                                    width: UIScreen.main.bounds.width,
                                    height: (imageHeight != nil) ? CGFloat(imageHeight!) : 400
                                )
                        } else {
                            Placeholder(type: .movie)
                        }
                        
                        LinearGradient(
                            gradient: Gradient(colors: [.black, .clear, .clear]),
                            startPoint: .top,
                            endPoint: .bottom
                        )
                        .frame(height: 300)
                    }
                }
                .padding(.top, 16)
                .background(Color.black)
            }
            .listRowInsets(EdgeInsets())
            
            let contentList = [
                Content.exampleText,
                Content.exampleVideo,
                Content.exampleQuote,
                Content.exampleMovieList,
                Content.examplePersonList,
                Content.exampleBlockSpecial,
                Content.exampleLinkScreen
            ]
            
            PageContentListView(contentList: contentList)
                .background(Color.white)
                .listRowInsets(EdgeInsets())
        }
        .listRowInsets(EdgeInsets())
    }
}

struct HistoryPageItemView_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopicItem = MainTopic.example(.history_cinema) as! MainTopicItem
        let summary = SummaryModel.example
        
        HistoryPageItemView(mainTopic: mainTopicItem, summary: summary)
    }
}
