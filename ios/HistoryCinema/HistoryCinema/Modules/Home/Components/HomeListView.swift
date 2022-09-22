//
//  PageView.swift
//  Landmarks
//
//  Created by Tiago Henrique da Silva on 07/02/21.
//

import SwiftUI

struct HomeListView<Content : View>: View {
    var homeContentItemList: [HomeContentItem]
    @ViewBuilder var HomeItemDestination: (_ mainTopicType: MainTopicsType) -> Content
    
    @State private var currentPage = 0
    
    var body: some View {
        VStack {
            ForEach(0 ..< homeContentItemList.count) { value in
                let homeContentItem = homeContentItemList[value]

                NavigationLink(destination: HomeItemDestination(homeContentItem.mainTopicType)) {
                    HomeItem(homeContentItem: homeContentItem)
                }
            }
        }
    }
}

struct PageView_Previews: PreviewProvider {
    static var previews: some View {
        let homeItemList: [HomeContentItem] = [
            HomeContentItem.example(MainTopicsType.history_cinema),
            HomeContentItem.example(MainTopicsType.mil_movies),
            HomeContentItem.example(MainTopicsType.awards),
            HomeContentItem.example(MainTopicsType.directors),
            HomeContentItem.example(MainTopicsType.timeline)
        ]
        
        HomeListView(homeContentItemList: homeItemList) { _ in
            AnyView(EmptyView())
        }
    }
}
