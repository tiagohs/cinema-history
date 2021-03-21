//
//  Path+Extensions.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 15/03/21.
//

import Foundation
import SwiftUI

extension Path {
    func scaled(toFit rect: CGRect) -> Path {
        let scaleW = rect.width/boundingRect.width
        let scaleH = rect.height/boundingRect.height
        let scaleFactor = min(scaleW, scaleH)
        return applying(CGAffineTransform(scaleX: scaleFactor, y: scaleFactor))
    }
}
