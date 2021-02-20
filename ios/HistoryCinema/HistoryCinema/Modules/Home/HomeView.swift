//
//  HomeView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import SwiftUI
import Combine

struct HomeView: View, HomeViewInterface {
    
    @ObservedObject private var presenter: HomePresenter = HomeWireframe.buildPresenter()
    
    var body: some View {
        ZStack(alignment: .leading) {
            if (presenter.homeContent.count == 0) {
                ProgressView()
            }
            
            if !presenter.homeContent.isEmpty {
                HomePageView(homeContentItemList: presenter.homeContent)
            }
        }
        .ignoresSafeArea()
        .background(Color.black)
        .onAppear { presenter.viewAppears() }
        .onDisappear { presenter.viewDisappears() }
    }
}

#if DEBUG
    struct HomeView_Previews: PreviewProvider {
        static var previews: some View {
            HomeView()
        }
    }
#endif
