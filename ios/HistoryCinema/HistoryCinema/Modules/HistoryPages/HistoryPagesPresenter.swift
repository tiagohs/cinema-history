//
//  HistoryPagesPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import Foundation
import Combine

// MARK: SummaryPresenter

class HistoryPagesPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: HistoryPagesInteractor?
    private var wireframe: HistoryPagesWireframe?
    
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: HistoryPagesInteractor, _ wireframe: HistoryPagesWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
    }
}

extension HistoryPagesPresenter {
    
}

