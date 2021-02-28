//
//  MainTopicQuote.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import SwiftUI

struct MainTopicQuoteView: View {
    let quoteMainTopic: QuoteMainTopic
    
    var body: some View {
        QuoteView(quote: quoteMainTopic.quote)
    }
}

struct MainTopicQuote_Previews: PreviewProvider {
    static var previews: some View {
        MainTopicQuoteView(quoteMainTopic: QuoteMainTopic.example)
            .background(Color.black)
    }
}
