//
//  PageView.swift
//  Landmarks
//
//  Created by Tiago Henrique da Silva on 07/02/21.
//

import SwiftUI

struct HomePageView: View {
    var homeContentItemList: [HomeContentItem]
    @State private var currentPage = 0
    
    var body: some View {
        ZStack(alignment: .bottomLeading) {
            let pages = homeContentItemList.map { homeContentItem in
                HomeItem(homeContentItem: homeContentItem)
            }
            
            PageViewController(pages: pages, currentPage: $currentPage)
            PageControl(numberOfPages: pages.count, currentPage: $currentPage)
                .frame(width: CGFloat(pages.count * 18))
                .padding(.trailing)
                .padding(.bottom, 80)
                .padding(.leading, 16)
                
        }
    }
}

struct PageView_Previews: PreviewProvider {
    static var previews: some View {
        let homeItemList: [HomeContentItem] = load("homecontent.json")
        
        HomePageView(homeContentItemList: homeItemList)
    }
}
