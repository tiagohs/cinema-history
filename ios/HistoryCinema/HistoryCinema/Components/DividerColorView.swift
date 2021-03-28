//
//  DividerColorView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/03/21.
//

import SwiftUI

struct DividerColorView: View {
    var colorName: String?
    
    var body: some View {
        let cN = colorName ?? ColorUtils.getRandomColorAssets().colorName
        
        HStack(spacing: 0) {
            Rectangle()
                .fill(Color(UIColor(colorName: "md_\(cN)_500")))
                .frame(height: 20)
            Rectangle()
                .fill(Color(UIColor(colorName: "md_\(cN)_600")))
                .frame(height: 20)
            Rectangle()
                .fill(Color(UIColor(colorName: "md_\(cN)_700")))
                .frame(height: 20)
            Rectangle()
                .fill(Color(UIColor(colorName: "md_\(cN)_800")))
                .frame(height: 20)
            Rectangle()
                .fill(Color(UIColor(colorName: "md_\(cN)_900")))
                .frame(height: 20)
        }
    }
}

struct DividerColorView_Previews: PreviewProvider {
    static var previews: some View {
        DividerColorView()
    }
}
