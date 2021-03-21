//
//  ContentQuoteView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import SwiftUI

struct ContentQuoteView: View {
    let contentQuote: ContentQuote
    
    var body: some View {
        let iconColorName = ColorUtils.getRandomColorAssets().colorName
        
        VStack {
            QuoteView(
                quote: contentQuote.quote,
                defaultTextColor: Color.black,
                defaultIconColor: Color(UIColor(colorName: "md_\(iconColorName)_500")))
        }
        .padding(.vertical, 16)
    }
}

struct ContentQuoteView_Previews: PreviewProvider {
    static var previews: some View {
        ContentQuoteView(contentQuote: Content.exampleQuote)
    }
}
