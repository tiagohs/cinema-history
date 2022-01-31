//
//  ViewDidLoadModifier.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 30/01/22.
//

import Foundation
import SwiftUI

struct ViewDidLoadModifier: ViewModifier {

    @State private var didLoad = false
    private let action: (() -> Void)?

    init(perform action: (() -> Void)? = nil) {
        self.action = action
    }
    
    func body(content: Self.Content) -> some View {
        content.onAppear {
            if didLoad == false {
                didLoad = true
                action?()
            }
        }
    }

}
