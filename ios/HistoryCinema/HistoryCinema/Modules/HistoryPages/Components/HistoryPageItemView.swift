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
    let page: Page
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    var onClickListLink: ((TextViewLinkScreenType, Int?) -> Void)? = nil
    
    var body: some View {
        let imageHeight = summary.image?.imageStyle?.height
        let imageURL = summary.image?.url
        
        ScrollView {
            VStack(alignment: .center) {
                VStack(alignment: .center) {
                    
                    Text(summary.title)
                        .textCase(.uppercase)
                        .frame(alignment: .center)
                        .fixedSize(horizontal: false, vertical: true)
                        .font(.proximaNovaBold(size: 22))
                        .multilineTextAlignment(.center)
                        .foregroundColor(Color.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                    
                    Text(summary.description)
                        .frame(alignment: .center)
                        .font(.proximaNovaRegular(size: 14))
                        .fixedSize(horizontal: false, vertical: true)
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
                                .shadow(color: .black, radius: 5)
                        } else {
                            Placeholder(type: .movie)
                                .shadow(color: .black, radius: 5)
                        }
                        
                        LinearGradient(
                            gradient: Gradient(colors: [.black, .clear, .clear]),
                            startPoint: .top,
                            endPoint: .bottom
                        )
                        .frame(height: (imageHeight != nil) ? CGFloat(imageHeight!) : 400)
                    }
                }
                .padding(.top, 16)
                .background(Color.black)
                
                PageContentListView(
                    contentList: page.contentList,
                    onClickLink: onClickLink,
                    onClickListLink: onClickListLink)
            }
        }
    }
}

struct HistoryPageItemView_Previews: PreviewProvider {
    
    static func getPage() -> Page {
        let page = Page.example
        
        page.contentList = [
            Content.exampleText,
            Content.exampleImage,
            Content.exampleVideo,
            Content.exampleQuote,
            Content.exampleMovieList,
            Content.examplePersonList,
            Content.exampleBlockSpecial,
            Content.exampleLinkScreen,
            Content.exampleRecomendation,
            Content.exampleNomineesMovies,
            Content.exampleNomineesPersons
        ]
        
        return page
    }
    
    static var previews: some View {
        
        HistoryPageItemView(
            mainTopic: MainTopic.example(.history_cinema) as! MainTopicItem,
            summary: SummaryModel.example,
            page: getPage()
        )
            .environment(\.colorScheme, .dark)
    }
}
