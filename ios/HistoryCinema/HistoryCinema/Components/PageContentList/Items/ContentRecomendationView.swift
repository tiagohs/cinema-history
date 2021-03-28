//
//  ContentRecomendationView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/03/21.
//

import SwiftUI

struct ContentRecomendationView: View {
    let contentRecomendation: ContentRecomendation
    
    @Environment(\.openURL) var openURL
    
    var body: some View {
        let list = contentRecomendation.list ?? []
        
        VStack {
            HStack(alignment: .center) {
                Image(systemName: "checkmark.circle.fill")
                    .font(.system(size: 22))
                    .foregroundColor(Color.black)
                
                Text("Recomendações e leitura adicional")
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .font(.oswaldBold(size: 16))
                    .foregroundColor(Color(UIColor(colorName: "md_black_1000")))
            }
            .padding(.horizontal, 16)
            
            ForEach(0 ..< list.count) { index in
                let item = list[index]
                let link = item.link
                
                LinkScreenView(
                    subtitle: item.subtitle,
                    title: item.title,
                    description: item.description,
                    onButtonClicked: {
                        openURL(URL(string: link!)!)
                    }
                )
            }
        }
        .padding(.vertical, 16)
    }
}

struct ContentRecomendationView_Previews: PreviewProvider {
    static var previews: some View {
        ContentRecomendationView(contentRecomendation: Content.exampleRecomendation)
    }
}
