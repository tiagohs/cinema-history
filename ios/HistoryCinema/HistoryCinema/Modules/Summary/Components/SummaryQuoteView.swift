//
//  SummaryQuoteView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/02/21.
//

import SwiftUI

struct SummaryQuoteView: View {
    let quote: Quote!
    
    var body: some View {
        ZStack {
            VStack {
                HStack(alignment: .top) {
                    Icon(name: .quote)
                        .fill((quote.iconColor != nil) ? Color(UIColor(colorName: quote.iconColor!)) : Color.white)
                        .frame(width: 50, height: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                        .padding(.horizontal)
                    
                    Spacer()
                }
                .zIndex(1)
                .offset(x: 10, y: 35)
                
                VStack {
                    Text(quote.quote ?? "")
                        .frame(maxWidth: .infinity, alignment: .center)
                        .font(.proximaNovaRegular(size: 16))
                        .multilineTextAlignment(.center)
                        .padding(.top, 32)
                        .padding(.horizontal, 12)
                        .foregroundColor((quote.textColor != nil) ? Color(UIColor(colorName: quote.textColor!)) : Color.white)
                    
                    Text(quote.author ?? "")
                        .frame(maxWidth: .infinity, alignment: .center)
                        .font(.oswaldRegular(size: 20))
                        .padding(.horizontal)
                        .padding(.bottom, 32)
                        .foregroundColor((quote.textColor != nil) ? Color(UIColor(colorName: quote.textColor!)) : Color.white)
                }
                .background((quote.backgroundColor != nil) ? Color(UIColor(colorName: quote.backgroundColor!)) : Color.white)
                .cornerRadius(10)
                .frame(width: 200, alignment: .center)
                
                HStack {
                    Spacer()
                    
                    Icon(name: .quote)
                        .fill((quote.iconColor != nil) ? Color(UIColor(colorName: quote.iconColor!)) : Color.white)
                        .rotationEffect(Angle(degrees: 180))
                        .frame(width: 50, height: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                        .padding(.horizontal)
                }
                .offset(x: -10, y: -35)
            }
            .frame(width: 300, height: .infinity, alignment: .center)
        }
    }
}

struct SummaryQuoteView_Previews: PreviewProvider {
    static var previews: some View {
        SummaryQuoteView(quote: Quote.example)
    }
}
