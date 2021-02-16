//
//  HomeContract.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 14/02/21.
//

import Foundation
import Combine
import Alamofire
import SwiftUI

protocol HomeViewInterface: BaseViewInterface {
    
}

protocol HomePresenterInterface: BasePresenterInterface, ObservableObject {
    
    var view: HomeViewInterface? { get set }
    var interactor: HomeInteractorInterface? { get set }
    var wireframe: HomeWireframaInterface? { get set }
    
    var moviesPublised: Published<[Movie]> { get }
    var moviesPublisher: Published<[Movie]>.Publisher { get }
    
    func fetchPopularMovies()
}

protocol HomeInteractorInterface: BaseInteractorInterface {
    
}

protocol HomeWireframaInterface: BaseWireframeInterface {
    
    static func buildPresenter() -> HomePresenter
}
