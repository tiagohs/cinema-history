//
//  PageControl.swift
//  Landmarks
//
//  Created by Tiago Henrique da Silva on 07/02/21.
//

import SwiftUI

struct PageControl: UIViewRepresentable {
    var numberOfPages: Int
    @Binding var currentPage: Int
    var pageIndicatorTintColor: UIColor?
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    func makeUIView(context: Context) -> UIPageControl {
        let control = UIPageControl()
        
        if let color = pageIndicatorTintColor {
            control.pageIndicatorTintColor = color
            control.currentPageIndicatorTintColor = color
        }
        
        control.numberOfPages = numberOfPages
        control.addTarget(
                    context.coordinator,
                    action: #selector(Coordinator.updateCurrentPage(sender:)),
                    for: .valueChanged)

        return control
    }

    func updateUIView(_ uiView: UIPageControl, context: Context) {
        uiView.currentPage = currentPage
    }
    
    class Coordinator: NSObject {
        var control: PageControl

        init(_ control: PageControl) {
            self.control = control
        }

        @objc
        func updateCurrentPage(sender: UIPageControl) {
            control.currentPage = sender.currentPage
        }
    }
}