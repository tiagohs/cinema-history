//
//  AboutPresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/10/22.
//

import Foundation
import Combine
import SwiftUI

// MARK: GlossaryPresenter

class AboutPresenter: BasePresenter, ObservableObject {
    
    // MARK: Properties
    
    private var interactor: AboutInteractor?
    private var wireframe: AboutWireframe?
    
    @Published var showErrorMessage: Bool = false
    
    init(_ interactor: AboutInteractor, _ wireframe: AboutWireframe) {
        self.interactor = interactor
        self.wireframe = wireframe
    }
}
