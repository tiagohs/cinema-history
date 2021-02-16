//
//  HomePresenter.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 16/02/21.
//

import Foundation
import Combine

class BasePresenter: ObservableObject {
    
    var cancalables: Set<AnyCancellable> = []
    
    func viewAppears() {
        
    }
    
    func viewDisappears() {
        cancalables.forEach { $0.cancel() }
    }
}
