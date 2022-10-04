//
//  MovieContentContainer.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 30/09/22.
//

import SwiftUI

struct MovieContentContainer<Content : View>: View {
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

struct MovieContentContainer_Previews: PreviewProvider {
    static var previews: some View {
        MovieContentContainer() {
            
        }
    }
}
