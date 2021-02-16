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
        VStack {
            Image("im_the_good_the_bad_and_the_ugly_scene")
                .resizable()
                .frame(height: 300, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
        }
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
