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
                case .image:
                    ContentImageView(contentImage: contentItem as! ContentImage)
                case .video:
                    ContentVideoView(contentVideo: contentItem as! ContentVideo)
                case .quote:
                    ContentQuoteView(contentQuote: contentItem as! ContentQuote)
                case .movie_list:
                    ContentMovieListView(contentMovieList: contentItem as! ContentMovieList)
                case .person_list:
                    ContentPersonListView(contentPersonList: contentItem as! ContentPersonList)
                case .block_special:
                    ContentBlockSpecialView(contentBlockSpecial: contentItem as! ContentBlockSpecial)
                        .padding(.horizontal, 16)
                        .listRowInsets(EdgeInsets())
                case .link_screen:
                    ContentLinkScreenView(contentLinkScreen: contentItem as! ContentLinkScreen)
                        .padding(.horizontal, 16)
                        .listRowInsets(EdgeInsets())
                case .movie_list_special:
                    ContentMovieListSpecialView(contentMovieListSpecial: contentItem as! ContentMovieListSpecial)
                default:
                    EmptyView()
                }
            }
            .background(Color.white)
    }
}

struct PageContentListView_Previews: PreviewProvider {
    static var previews: some View {
        let contentList = [
            Content.exampleText,
            Content.exampleImage,
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
