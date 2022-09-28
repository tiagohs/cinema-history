//
//  PageLinkModel.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/09/22.
//

import Foundation

class PageLinkModel: Identifiable {
    let id: Int?
    let type: TextViewLinkScreenType?
    init(_ id: Int?, _ type: TextViewLinkScreenType?) {
        self.id = id
        self.type = type
    }
}

