//
//  HomeWireframe.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import SwiftUI

class HomeWireframe {
    
    @ViewBuilder
    func presentMainTopics(_ mainTopicType: MainTopicsType) -> some View {
        MainTopicsView(
            mainTopicType: mainTopicType
        )
    }
    
    @ViewBuilder
    func presentTimeline() -> some View {
        TimelinesView()
    }
    
    @ViewBuilder
    func presentReferences() -> some View {
        ReferencesView()
    }
    
    @ViewBuilder
    func presentGlossary() -> some View {
        GlossaryView()
    }
    
    @ViewBuilder
    func presentAbout() -> some View {
        AboutView()
    }
    
}

// MARK: build's Module

extension HomeWireframe {
    
    static func buildPresenter() -> HomePresenter {
        let localContentService = LocalContentService()
        let interactor = HomeInteractor(localContentService)
        let wireframe = HomeWireframe()
        
        return HomePresenter(interactor, wireframe)
    }
}
