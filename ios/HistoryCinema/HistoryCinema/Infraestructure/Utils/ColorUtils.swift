//
//  ColorUtils.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import Foundation

class ColorUtils {
    
    static func getRandomColorAssets() -> ColorAsset {
        let listOfColors = [
            ColorAsset(colorName: "red", textColorName: "md_white_1000"),
            ColorAsset(colorName: "pink", textColorName: "md_white_1000"),
            ColorAsset(colorName: "purple", textColorName: "md_white_1000"),
            ColorAsset(colorName: "deep_purple", textColorName: "md_white_1000"),
            ColorAsset(colorName: "blue", textColorName: "md_white_1000"),
            ColorAsset(colorName: "light_blue", textColorName: "md_white_1000"),
            ColorAsset(colorName: "cyan", textColorName: "md_white_1000"),
            ColorAsset(colorName: "teal", textColorName: "md_white_1000"),
            ColorAsset(colorName: "green", textColorName: "md_white_1000"),
            ColorAsset(colorName: "light_green", textColorName: "md_black_1000"),
            ColorAsset(colorName: "lime", textColorName: "md_white_1000"),
            ColorAsset(colorName: "yellow", textColorName: "md_black_1000"),
            ColorAsset(colorName: "amber", textColorName: "md_black_1000"),
            ColorAsset(colorName: "orange", textColorName: "md_black_1000"),
            ColorAsset(colorName: "deep_orange", textColorName: "md_white_1000"),
            ColorAsset(colorName: "grey", textColorName: "md_white_1000"),
            ColorAsset(colorName: "blue_grey", textColorName: "md_white_1000")
        ]

        return listOfColors.randomElement() ?? ColorAsset(colorName: "red", textColorName: "md_white_1000")
    }
}
