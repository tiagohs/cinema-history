//
//  TimelinesPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 23/10/22.
//

import Foundation
import Combine
import SwiftUI

// MARK: GlossaryPresenter

class TimelinesPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: TimelinesInteractor?
    private var wireframe: TimelinesWireframe?
    
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: TimelinesInteractor, _ wireframe: TimelinesWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
    }
}
