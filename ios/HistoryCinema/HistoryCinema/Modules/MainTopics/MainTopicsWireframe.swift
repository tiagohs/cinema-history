//
//  MainTopicsWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import SwiftUI

class MainTopicsWireframe {
    
    @ViewBuilder
    func presentSummary(_ mainTopic: MainTopicItem) -> some View {
        SummaryView(mainTopic: mainTopic)
            .navigationBarTitle("")
            .navigationBarHidden(true)
    }
    
    @ViewBuilder
    func presentMilMovies(_ mainTopic: MilMoviesMainTopic) -> some View {
        MilMoviesView(mainTopic: mainTopic)
            .navigationBarTitle("")
            .navigationBarHidden(true)
    }
}

// MARK: build's Module

extension MainTopicsWireframe {
    
    static func buildPresenter() -> MainTopicsPresenter {
        let localContentService = LocalContentService()
        let interactor = MainTopicsInteractor(localContentService)
        let wireframe = MainTopicsWireframe()
        
        return MainTopicsPresenter(interactor, wireframe)
    }
}
