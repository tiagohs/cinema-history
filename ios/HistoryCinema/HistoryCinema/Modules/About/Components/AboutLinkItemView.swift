//
//  AboutLinkItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/10/22.
//

import SwiftUI

struct AboutLinkItemView: View {
    
    let link: String!
    let text: String!
    
    var body: some View {
        Link(destination: URL(string: link)!) {
            Text(text)
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.textPrimary)
        }
        .buttonStyle(.borderless)
    }
}

struct AboutLinkItemView_Previews: PreviewProvider {
    static var previews: some View {
        AboutLinkItemView(
            link: "https://animated-stickers.s3-sa-east-1.amazonaws.com/history_terms.html",
            text: "Termos do Aplicativo"
        )
    }
}
