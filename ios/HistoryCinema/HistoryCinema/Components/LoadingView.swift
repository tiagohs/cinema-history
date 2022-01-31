//
//  LoadingView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 31/01/22.
//

import SwiftUI


struct LoadingView: View {
    var description: String = ""
    var scaleAmount: Double = 1.0
    
    var body: some View {
        ProgressView(description)
            .scaleEffect(scaleAmount, anchor: .center)
            .progressViewStyle(CircularProgressViewStyle(tint: Color.black))
            .foregroundColor(Color(UIColor.init(colorName: "md_black_1000")))
    }
}

struct LoadingView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingView(description: "Carregando")
    }
}
