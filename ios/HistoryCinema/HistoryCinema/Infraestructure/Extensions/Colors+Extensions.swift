//
//  Colors+Extensions.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import Foundation
import UIKit
import SwiftUI

extension UIColor {
    
    public convenience init(hex: String) {
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        
        if (cString.hasPrefix("#")) {
            cString.remove(at: cString.startIndex)
        }
        var r: CGFloat = 0.0
        var g: CGFloat = 0.0
        var b: CGFloat = 0.0
        var a: CGFloat = 1.0
        
        var rgbValue:UInt64 = 0
        Scanner(string: cString).scanHexInt64(&rgbValue)
        
        if ((cString.count) == 8) {
            r = CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0
            g =  CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0
            b = CGFloat((rgbValue & 0x0000FF)) / 255.0
            a = CGFloat((rgbValue & 0xFF000000)  >> 24) / 255.0
            
        }else if ((cString.count) == 6){
            r = CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0
            g =  CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0
            b = CGFloat((rgbValue & 0x0000FF)) / 255.0
            a =  CGFloat(1.0)
        }
        
        
        self.init(  red: r,
                    green: g,
                    blue: b,
                    alpha: a
        )
    }
    
    public convenience init(colorName: String) {
        self.init(hex: Constants.colors[colorName]!)
    }
}

extension Color {
    
    static var randomColor: Color {
        let colorNames = [
            "md_red_500",
            "md_pink_500",
            "md_purple_500",
            "md_deep_purple_500",
            "md_indigo_500",
            "md_blue_500",
            "md_light_blue_500",
            "md_cyan_500",
            "md_teal_500",
            "md_green_500",
            "md_light_green_500",
            "md_lime_500",
            "md_yellow_500",
            "md_amber_500",
            "md_orange_500",
            "md_deep_orange_500",
            "md_brown_500",
            "md_grey_500",
            "md_blue_grey_500"
        ]
        
        return Color(UIColor(colorName: colorNames.randomElement() ?? "md_blue_500"))
    }
    
    static var textPrimary: Color  {
        return Color("textPrimary")
    }
    
    static func textPrimaryHEX(_ colorScheme: ColorScheme) -> String {
        return colorScheme == .dark ? "#FFF" : "#000"
    }
    
    static var textSecondary: Color  {
        return Color("textSecondary")
    }
    
    static func textSecondaryHEX(_ colorScheme: ColorScheme) -> String {
        return colorScheme == .dark ? "#E0E0E0" : "#616161"
    }
    
    static var textInverse: Color  {
        return Color("textInverse")
    }
    
    static var textDark: Color  {
        return Color("textSecondary")
    }
    
    static func textDarkHEX(_ colorScheme: ColorScheme) -> String {
        return colorScheme == .dark ? "#F5F5F5" : "#212121"
    }
    
    static var cardTextPrimary: Color  {
        return Color("cardTextPrimary")
    }
    
    static var cardBackground: Color  {
        return Color("cardBackground")
    }
    
    static var cardButtonTextColor: Color  {
        return Color("cardButtonTextColor")
    }
    
    static var cardButtonBackgroundColor: Color  {
        return Color("cardButtonBackgroundColor")
    }
    
    static var backgroundInverse: Color  {
        return Color("backgroundInverse")
    }
    
    static var backgroundPrimary: Color  {
        return Color("backgroundPrimary")
    }
    
    static var bottomHistoryNavigationBackgroundColor: Color  {
        return Color("bottomHistoryNavigationBackgroundColor")
    }
    
    static var bottomHistoryNavigationTextColor: Color  {
        return Color("bottomHistoryNavigationTextColor")
    }
    
    static var shadowColor: Color  {
        return Color("shadowColor")
    }
    
    func toHex() -> String? {
            let uic = UIColor(self)
            guard let components = uic.cgColor.components, components.count >= 3 else {
                return nil
            }
            let r = Float(components[0])
            let g = Float(components[1])
            let b = Float(components[2])
            var a = Float(1.0)

            if components.count >= 4 {
                a = Float(components[3])
            }

            if a != Float(1.0) {
                return String(format: "%02lX%02lX%02lX%02lX", lroundf(r * 255), lroundf(g * 255), lroundf(b * 255), lroundf(a * 255))
            } else {
                return String(format: "%02lX%02lX%02lX", lroundf(r * 255), lroundf(g * 255), lroundf(b * 255))
            }
        }
}



