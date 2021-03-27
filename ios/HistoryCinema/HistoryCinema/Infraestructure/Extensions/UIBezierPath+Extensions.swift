//
//  UIBezierPath+Extensions.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 15/03/21.
//

import Foundation
import SwiftUI

extension UIBezierPath {
    
    static var quote: UIBezierPath {
        let path = UIBezierPath()
        path.move(to: CGPoint(x: 30.5, y: 43.9))
        path.addCurve(to: CGPoint(x: 23.5, y: 42.9), controlPoint1: CGPoint(x: 28.2, y: 43.3), controlPoint2: CGPoint(x: 25.8, y: 42.9))
        path.addCurve(to: CGPoint(x: 14.8, y: 44.7), controlPoint1: CGPoint(x: 20, y: 42.9), controlPoint2: CGPoint(x: 17.1, y: 43.7))
        path.addCurve(to: CGPoint(x: 32.8, y: 21.2), controlPoint1: CGPoint(x: 17, y: 36.7), controlPoint2: CGPoint(x: 22.3, y: 22.8))
        path.addCurve(to: CGPoint(x: 34.8, y: 19.4), controlPoint1: CGPoint(x: 33.8, y: 21.1), controlPoint2: CGPoint(x: 34.6, y: 20.3))
        path.addLine(to: CGPoint(x: 37.1, y: 11.2))
        path.addCurve(to: CGPoint(x: 36.8, y: 9.1), controlPoint1: CGPoint(x: 37.3, y: 10.5), controlPoint2: CGPoint(x: 37.2, y: 9.7))
        path.addCurve(to: CGPoint(x: 35.1, y: 8), controlPoint1: CGPoint(x: 36.4, y: 8.5), controlPoint2: CGPoint(x: 35.8, y: 8.1))
        path.addCurve(to: CGPoint(x: 32.7, y: 7.9), controlPoint1: CGPoint(x: 34.3, y: 7.9), controlPoint2: CGPoint(x: 33.5, y: 7.9))
        path.addCurve(to: CGPoint(x: 2.3, y: 39.9), controlPoint1: CGPoint(x: 20.1, y: 7.9), controlPoint2: CGPoint(x: 7.6, y: 21))
        path.addCurve(to: CGPoint(x: 5.9, y: 78.2), controlPoint1: CGPoint(x: -0.8, y: 51), controlPoint2: CGPoint(x: -1.7, y: 67.7))
        path.addCurve(to: CGPoint(x: 24.4, y: 87.5), controlPoint1: CGPoint(x: 10.2, y: 84), controlPoint2: CGPoint(x: 16.4, y: 87.2))
        path.addCurve(to: CGPoint(x: 24.5, y: 87.5), controlPoint1: CGPoint(x: 24.5, y: 87.5), controlPoint2: CGPoint(x: 24.5, y: 87.5))
        path.addCurve(to: CGPoint(x: 45.9, y: 71.3), controlPoint1: CGPoint(x: 34.4, y: 87.5), controlPoint2: CGPoint(x: 43.2, y: 80.8))
        path.addCurve(to: CGPoint(x: 43.8, y: 54.4), controlPoint1: CGPoint(x: 47.5, y: 65.6), controlPoint2: CGPoint(x: 46.8, y: 59.6))
        path.addCurve(to: CGPoint(x: 30.5, y: 43.9), controlPoint1: CGPoint(x: 41, y: 49.3), controlPoint2: CGPoint(x: 36.2, y: 45.6))
        path.close()

        let path2 = UIBezierPath()
        path2.move(to: CGPoint(x: 92.5, y: 54.4))
        path2.addCurve(to: CGPoint(x: 79.1, y: 43.9), controlPoint1: CGPoint(x: 89.6, y: 49.3), controlPoint2: CGPoint(x: 84.9, y: 45.6))
        path2.addCurve(to: CGPoint(x: 72.2, y: 42.9), controlPoint1: CGPoint(x: 76.8, y: 43.3), controlPoint2: CGPoint(x: 74.4, y: 42.9))
        path2.addCurve(to: CGPoint(x: 63.4, y: 44.7), controlPoint1: CGPoint(x: 68.6, y: 42.9), controlPoint2: CGPoint(x: 65.7, y: 43.7))
        path2.addCurve(to: CGPoint(x: 81.4, y: 21.2), controlPoint1: CGPoint(x: 65.6, y: 36.7), controlPoint2: CGPoint(x: 70.9, y: 22.8))
        path2.addCurve(to: CGPoint(x: 83.5, y: 19.4), controlPoint1: CGPoint(x: 82.4, y: 21.1), controlPoint2: CGPoint(x: 83.2, y: 20.3))
        path2.addLine(to: CGPoint(x: 85.8, y: 11.2))
        path2.addCurve(to: CGPoint(x: 85.4, y: 9.1), controlPoint1: CGPoint(x: 86, y: 10.5), controlPoint2: CGPoint(x: 85.8, y: 9.7))
        path2.addCurve(to: CGPoint(x: 83.7, y: 8), controlPoint1: CGPoint(x: 85, y: 8.5), controlPoint2: CGPoint(x: 84.4, y: 8.1))
        path2.addCurve(to: CGPoint(x: 81.3, y: 7.9), controlPoint1: CGPoint(x: 82.9, y: 7.9), controlPoint2: CGPoint(x: 82.1, y: 7.9))
        path2.addCurve(to: CGPoint(x: 50.9, y: 39.9), controlPoint1: CGPoint(x: 68.7, y: 7.9), controlPoint2: CGPoint(x: 56.2, y: 21))
        path2.addCurve(to: CGPoint(x: 54.5, y: 78.2), controlPoint1: CGPoint(x: 47.8, y: 51), controlPoint2: CGPoint(x: 46.9, y: 67.7))
        path2.addCurve(to: CGPoint(x: 73.1, y: 87.5), controlPoint1: CGPoint(x: 58.8, y: 84), controlPoint2: CGPoint(x: 65, y: 87.2))
        path2.addCurve(to: CGPoint(x: 73.2, y: 87.5), controlPoint1: CGPoint(x: 73.1, y: 87.5), controlPoint2: CGPoint(x: 73.1, y: 87.5))
        path2.addCurve(to: CGPoint(x: 94.5, y: 71.3), controlPoint1: CGPoint(x: 83.1, y: 87.5), controlPoint2: CGPoint(x: 91.8, y: 80.8))
        path2.addCurve(to: CGPoint(x: 92.5, y: 54.4), controlPoint1: CGPoint(x: 96.1, y: 65.6), controlPoint2: CGPoint(x: 95.4, y: 59.6))
        path2.close()

        path.append(path2)
        
        return path
    }
    
    static var movie: UIBezierPath {
        let path = UIBezierPath()
        path.move(to: CGPoint(x: 465.6, y: 139.3))
        path.addLine(to: CGPoint(x: 125.6, y: 139.3))
        path.addLine(to: CGPoint(x: 125.1, y: 139.5))
        path.addLine(to: CGPoint(x: 24.1, y: 139.5))
        path.addCurve(to: CGPoint(x: 3.6, y: 160), controlPoint1: CGPoint(x: 12.8, y: 139.5), controlPoint2: CGPoint(x: 3.6, y: 148.7))
        path.addLine(to: CGPoint(x: 3.6, y: 187.4))
        path.addLine(to: CGPoint(x: 3.6, y: 469.2))
        path.addLine(to: CGPoint(x: 3.6, y: 469.4))
        path.addLine(to: CGPoint(x: 3.6, y: 469.4))
        path.addCurve(to: CGPoint(x: 24.1, y: 489.7), controlPoint1: CGPoint(x: 3.7, y: 480.6), controlPoint2: CGPoint(x: 12.8, y: 489.7))
        path.addLine(to: CGPoint(x: 465.6, y: 489.7))
        path.addCurve(to: CGPoint(x: 486.1, y: 469.2), controlPoint1: CGPoint(x: 476.9, y: 489.7), controlPoint2: CGPoint(x: 486.1, y: 480.5))
        path.addLine(to: CGPoint(x: 486.1, y: 159.8))
        path.addCurve(to: CGPoint(x: 465.6, y: 139.3), controlPoint1: CGPoint(x: 486.1, y: 148.5), controlPoint2: CGPoint(x: 476.9, y: 139.3))
        path.close()
        path.move(to: CGPoint(x: 116.4, y: 241.6))
        path.addLine(to: CGPoint(x: 44.8, y: 241.6))
        path.addLine(to: CGPoint(x: 80.6, y: 163.2))
        path.addLine(to: CGPoint(x: 152.3, y: 163.2))
        path.addLine(to: CGPoint(x: 116.4, y: 241.6))
        path.close()
        path.move(to: CGPoint(x: 223.5, y: 163.2))
        path.addLine(to: CGPoint(x: 295.1, y: 163.2))
        path.addLine(to: CGPoint(x: 259.3, y: 241.6))
        path.addLine(to: CGPoint(x: 187.7, y: 241.6))
        path.addLine(to: CGPoint(x: 223.5, y: 163.2))
        path.close()
        path.move(to: CGPoint(x: 310.1, y: 354.4))
        path.addLine(to: CGPoint(x: 233.1, y: 410.4))
        path.addCurve(to: CGPoint(x: 222.7, y: 413.7), controlPoint1: CGPoint(x: 230, y: 412.6), controlPoint2: CGPoint(x: 226.4, y: 413.7))
        path.addCurve(to: CGPoint(x: 214.8, y: 411.8), controlPoint1: CGPoint(x: 220, y: 413.7), controlPoint2: CGPoint(x: 217.3, y: 413.1))
        path.addCurve(to: CGPoint(x: 205.2, y: 396.1), controlPoint1: CGPoint(x: 208.9, y: 408.8), controlPoint2: CGPoint(x: 205.2, y: 402.7))
        path.addLine(to: CGPoint(x: 205.2, y: 284.3))
        path.addCurve(to: CGPoint(x: 214.8, y: 268.6), controlPoint1: CGPoint(x: 205.2, y: 277.7), controlPoint2: CGPoint(x: 208.9, y: 271.6))
        path.addCurve(to: CGPoint(x: 233.1, y: 270), controlPoint1: CGPoint(x: 220.6, y: 265.6), controlPoint2: CGPoint(x: 227.7, y: 266.2))
        path.addLine(to: CGPoint(x: 310.1, y: 326))
        path.addCurve(to: CGPoint(x: 317.3, y: 340.2), controlPoint1: CGPoint(x: 314.6, y: 329.3), controlPoint2: CGPoint(x: 317.3, y: 334.6))
        path.addCurve(to: CGPoint(x: 310.1, y: 354.4), controlPoint1: CGPoint(x: 317.3, y: 345.8), controlPoint2: CGPoint(x: 314.6, y: 351.1))
        path.close()
        path.move(to: CGPoint(x: 402.2, y: 241.6))
        path.addLine(to: CGPoint(x: 330.6, y: 241.6))
        path.addLine(to: CGPoint(x: 366.4, y: 163.2))
        path.addLine(to: CGPoint(x: 438, y: 163.2))
        path.addLine(to: CGPoint(x: 402.2, y: 241.6))
        path.close()

        let path2 = UIBezierPath()
        path2.move(to: CGPoint(x: 23.1, y: 122.9))
        path2.addLine(to: CGPoint(x: 464.5, y: 122.9))
        path2.addCurve(to: CGPoint(x: 484.1, y: 99.8), controlPoint1: CGPoint(x: 475.3, y: 122.9), controlPoint2: CGPoint(x: 484.1, y: 112.6))
        path2.addLine(to: CGPoint(x: 484.1, y: 23.1))
        path2.addCurve(to: CGPoint(x: 464.5, y: 0), controlPoint1: CGPoint(x: 484.1, y: 10.3), controlPoint2: CGPoint(x: 475.3, y: 0))
        path2.addLine(to: CGPoint(x: 23.1, y: 0))
        path2.addCurve(to: CGPoint(x: 3.6, y: 23.1), controlPoint1: CGPoint(x: 12.3, y: 0), controlPoint2: CGPoint(x: 3.6, y: 10.3))
        path2.addLine(to: CGPoint(x: 3.6, y: 99.8))
        path2.addCurve(to: CGPoint(x: 23.1, y: 122.9), controlPoint1: CGPoint(x: 3.6, y: 112.6), controlPoint2: CGPoint(x: 12.3, y: 122.9))
        path2.close()
        path2.move(to: CGPoint(x: 366.4, y: 22.3))
        path2.addLine(to: CGPoint(x: 438, y: 22.3))
        path2.addLine(to: CGPoint(x: 402.2, y: 100.6))
        path2.addLine(to: CGPoint(x: 330.6, y: 100.6))
        path2.addLine(to: CGPoint(x: 366.4, y: 22.3))
        path2.close()
        path2.move(to: CGPoint(x: 223.5, y: 22.3))
        path2.addLine(to: CGPoint(x: 295.1, y: 22.3))
        path2.addLine(to: CGPoint(x: 259.3, y: 100.6))
        path2.addLine(to: CGPoint(x: 187.7, y: 100.6))
        path2.addLine(to: CGPoint(x: 223.5, y: 22.3))
        path2.close()
        path2.move(to: CGPoint(x: 80.6, y: 22.3))
        path2.addLine(to: CGPoint(x: 152.3, y: 22.3))
        path2.addLine(to: CGPoint(x: 116.4, y: 100.6))
        path2.addLine(to: CGPoint(x: 44.8, y: 100.6))
        path2.addLine(to: CGPoint(x: 80.6, y: 22.3))
        path2.close()
        
        path.append(path2)
        
        return path
    }
    
    static var person: UIBezierPath {
        let path = UIBezierPath()
        path.move(to: CGPoint(x: 256, y: 0))
        path.addCurve(to: CGPoint(x: 121, y: 135), controlPoint1: CGPoint(x: 181.6, y: 0), controlPoint2: CGPoint(x: 121, y: 60.6))
        path.addCurve(to: CGPoint(x: 256, y: 270), controlPoint1: CGPoint(x: 121, y: 209.4), controlPoint2: CGPoint(x: 181.6, y: 270))
        path.addCurve(to: CGPoint(x: 391, y: 135), controlPoint1: CGPoint(x: 330.4, y: 270), controlPoint2: CGPoint(x: 391, y: 209.4))
        path.addCurve(to: CGPoint(x: 256, y: 0), controlPoint1: CGPoint(x: 391, y: 60.6), controlPoint2: CGPoint(x: 330.4, y: 0))
        path.close()
        
        let path2 = UIBezierPath()
        path2.move(to: CGPoint(x: 424, y: 358.2))
        path2.addCurve(to: CGPoint(x: 286, y: 300), controlPoint1: CGPoint(x: 387, y: 320.7), controlPoint2: CGPoint(x: 338, y: 300))
        path2.addLine(to: CGPoint(x: 226, y: 300))
        path2.addCurve(to: CGPoint(x: 88, y: 358.2), controlPoint1: CGPoint(x: 174, y: 300), controlPoint2: CGPoint(x: 125, y: 320.7))
        path2.addCurve(to: CGPoint(x: 31, y: 497), controlPoint1: CGPoint(x: 51.3, y: 395.5), controlPoint2: CGPoint(x: 31, y: 444.8))
        path2.addCurve(to: CGPoint(x: 46, y: 512), controlPoint1: CGPoint(x: 31, y: 505.3), controlPoint2: CGPoint(x: 37.7, y: 512))
        path2.addLine(to: CGPoint(x: 466, y: 512))
        path2.addCurve(to: CGPoint(x: 481, y: 497), controlPoint1: CGPoint(x: 474.3, y: 512), controlPoint2: CGPoint(x: 481, y: 505.3))
        path2.addCurve(to: CGPoint(x: 424, y: 358.2), controlPoint1: CGPoint(x: 481, y: 444.8), controlPoint2: CGPoint(x: 460.7, y: 395.5))
        path2.close()
        
        path.append(path2)

        return path
    }
}
