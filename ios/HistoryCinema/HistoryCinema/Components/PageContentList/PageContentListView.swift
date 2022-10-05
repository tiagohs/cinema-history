//
//  PageContentListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct PageContentListView: View {
    let contentList: [Content]
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    var onClickListLink: ((TextViewLinkScreenType, Int?) -> Void)? = nil
    
    var body: some View {
            ForEach(0 ..< contentList.count) { index in
                let contentItem = contentList[index]
                
                switch contentItem.type {
                case .text:
                    ContentTextView(
                        contentText: contentItem as! ContentText,
                        onClickLink: onClickLink
                    )
                        .fixedSize(horizontal: false, vertical: true)
                case .image:
                    ContentImageView(contentImage: contentItem as! ContentImage)
                case .video:
                    ContentVideoView(contentVideo: contentItem as! ContentVideo)
                case .quote:
                    ContentQuoteView(contentQuote: contentItem as! ContentQuote)
                case .movie_list:
                    ContentMovieListView(
                        contentMovieList: contentItem as! ContentMovieList,
                        onClickListLink: onClickListLink)
                case .person_list:
                    ContentPersonListView(
                        contentPersonList: contentItem as! ContentPersonList,
                        onClickLink: onClickListLink)
                case .block_special:
                    ContentBlockSpecialView(contentBlockSpecial: contentItem as! ContentBlockSpecial)
                        .padding(.horizontal, 16)
                case .link_screen:
                    ContentLinkScreenView(contentLinkScreen: contentItem as! ContentLinkScreen)
                        .padding(.horizontal, 16)
                        .listRowInsets(EdgeInsets())
                case .movie_list_special:
                    ContentMovieListSpecialView(contentMovieListSpecial: contentItem as! ContentMovieListSpecial)
                case .recomendations:
                    ContentRecomendationView(contentRecomendation: contentItem as! ContentRecomendation)
                case .awards_nominees:
                    ContentNomineesView(contentNominee: contentItem as! ContentNominee)
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
            Content.exampleImage,
            Content.exampleVideo,
            Content.exampleQuote,
            Content.exampleMovieList,
            Content.examplePersonList,
            Content.exampleBlockSpecial,
            Content.exampleLinkScreen,
            Content.exampleRecomendation,
            Content.exampleNomineesMovies,
            Content.exampleNomineesPersons,
            Content.exampleMovieSpecial
        ]
        
        PageContentListView(contentList: contentList)
    }
}
