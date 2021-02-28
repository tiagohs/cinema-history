//
//  QuoteView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import SwiftUI

struct QuoteView: View {
    let quote: Quote
    
    var body: some View {
        VStack {
            HStack {
                Image("ic_quote_black")
                    .renderingMode(.template)
                    .resizable()
                    .rotationEffect(Angle(degrees: 180))
                    .frame(width: 50, height: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    .padding(.horizontal)
                    .foregroundColor((quote.iconColor != nil) ? Color(UIColor(colorName: quote.iconColor!)) : Color.white)
                
                Spacer()
            }
            
            HStack {
                Spacer()
                
                Text(quote.quote ?? "")
                    .frame(maxWidth: .infinity, alignment: .center)
                    .font(.proximaNovaRegular(size: 16))
                    .multilineTextAlignment(.center)
                    .padding(.vertical, 12)
                    .padding(.horizontal)
                    .foregroundColor((quote.textColor != nil) ? Color(UIColor(hex: quote.textColor!)) : Color.white)
                
                Spacer()
            }
            
            HStack {
                Spacer()
                
                Text(quote.author ?? "")
                    .frame(maxWidth: .infinity, alignment: .center)
                    .font(.oswaldBold(size: 20))
                    .padding(.horizontal)
                    .foregroundColor((quote.textColor != nil) ? Color(UIColor(colorName: quote.textColor!)) : Color.white)
                
                Spacer()
            }
            
            HStack {
                Spacer()
                
                Image("ic_quote_black")
                    .renderingMode(.template)
                    .resizable()
                    .frame(width: 50, height: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    .padding(.horizontal)
                    .foregroundColor((quote.iconColor != nil) ? Color(UIColor(colorName: quote.iconColor!)) : Color.white)
            }
        }
        .padding(.vertical, 26)
    }
}

struct QuoteView_Previews: PreviewProvider {
    static var previews: some View {
        QuoteView(quote: Quote.example)
            .background(Color.black)
    }
}
