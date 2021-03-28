//
//  QuoteView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import SwiftUI

struct QuoteView: View {
    let quote: Quote
    var defaultTextColor: Color? = nil
    var defaultIconColor: Color? = nil
    
    var body: some View {
        VStack {
            HStack {
                Icon(name: .quote)
                    .fill((quote.iconColor != nil) ? Color(UIColor(colorName: quote.iconColor!)) : (defaultIconColor ?? Color.white))
                    .frame(width: 50, height: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    .padding(.horizontal)
                    
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
                    .fixedSize(horizontal: false, vertical: true)
                    .foregroundColor((quote.textColor != nil) ? Color(UIColor(hex: quote.textColor!)) : (defaultTextColor ?? Color.white))
                
                Spacer()
            }
            
            HStack {
                Spacer()
                
                Text(quote.author ?? "")
                    .frame(maxWidth: .infinity, alignment: .center)
                    .font(.oswaldBold(size: 20))
                    .padding(.horizontal)
                    .foregroundColor((quote.textColor != nil) ? Color(UIColor(colorName: quote.textColor!)) : (defaultTextColor ?? Color.white))
                
                Spacer()
            }
            
            HStack {
                Spacer()
                
                Icon(name: .quote)
                    .fill((quote.iconColor != nil) ? Color(UIColor(colorName: quote.iconColor!)) : (defaultIconColor ?? Color.white))
                    .frame(width: 50, height: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    .rotationEffect(Angle(degrees: 180))
                    .padding(.horizontal)
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