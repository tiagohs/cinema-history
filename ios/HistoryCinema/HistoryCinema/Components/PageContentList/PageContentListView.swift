//
//  PageContentListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct PageContentListView: View {
    let contentList: [Content]
    
    var body: some View {
            ForEach(0 ..< contentList.count) { index in
                let contentItem = contentList[index]
                
                switch contentItem.type {
                case .text:
                    ContentTextView(contentText: contentItem as! ContentText)
                case .video:
                    ContentVideoView(contentVideo: contentItem as! ContentVideo)
                case .quote:
                    ContentQuoteView(contentQuote: contentItem as! ContentQuote)
                case .movie_list:
                    ContentMovieListView(contentMovieList: contentItem as! ContentMovieList)
                default:
                    EmptyView()
                }
            }
    }
}

struct PageContentListView_Previews: PreviewProvider {
    static var previews: some View {
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
    }
}
