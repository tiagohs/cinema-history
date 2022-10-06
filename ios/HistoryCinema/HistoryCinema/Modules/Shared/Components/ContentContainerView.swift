//
//  ContentContainerView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 05/10/22.
//

import SwiftUI

struct ContentContainer<Content : View>: View {
    @ViewBuilder var content: () -> Content
    
    var body: some View {
        VStack(alignment: .leading) {
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.backgroundPrimary)
        .cornerRadius(12)
        .shadow(radius: 5)
        .padding(.horizontal, 10)
    }
}

struct ContentContainer_Previews: PreviewProvider {
    static var previews: some View {
        ContentContainer() {
            
        }
    }
}
