//
//  SummaryPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import Foundation

import Combine

// MARK: MainTopicsPresenter

class SummaryPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: SummaryInteractor?
    private var wireframe: SummaryWireframe?
    
    @Published var mainTopics: [MainTopic]
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: SummaryInteractor, _ wireframe: SummaryWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
        self.mainTopics = []
    }
}

extension SummaryPresenter {
    
}
