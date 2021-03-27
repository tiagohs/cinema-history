//
//  ContentLinkScreenView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/03/21.
//

import SwiftUI

struct ContentLinkScreenView: View {
    let contentLinkScreen: ContentLinkScreen
    
    var body: some View {
        VStack {
            LinkScreenView(
                subtitle: contentLinkScreen.subtitle,
                title: contentLinkScreen.title,
                description: contentLinkScreen.description,
                image: contentLinkScreen.image
            )
        }
        .padding(.vertical, 16)
    }
}

struct ContentLinkScreenView_Previews: PreviewProvider {
    static var previews: some View {
        ContentLinkScreenView(contentLinkScreen: Content.exampleLinkScreen)
    }
}
