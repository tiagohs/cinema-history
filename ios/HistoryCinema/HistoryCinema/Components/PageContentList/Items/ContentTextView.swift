//
//  ContentTextView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct ContentTextView: View {
    let contentText: ContentText
    
    @ViewBuilder
    func renderTitle() -> some View {
        let title = contentText.contentTitle
        
        if (title != nil) {
            VStack {
                HStack {
                    Text(title!)
                        .font(.oswaldBold(size: 22))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(Color.black)
                        .padding(.horizontal, 16)
                        .padding(.top, 16)
                    
                    Spacer()
                }
                
                HStack {
                    VStack {
                        Divider()
                            .frame(width: 150)
                            .padding(.vertical, 8)
                            .padding(.leading, 16)
                    }
                    
                    Spacer()
                }
            }
            
        } else {
            EmptyView()
        }
    }
    
    @ViewBuilder
    func renderText() -> some View {
        let text = contentText.contentText
        
        if (text != nil) {
            HStack {
                StyledTextView(
                    content: text!,
                    fontName: "ProximaNova-Regular",
                    size: 16,
                    color: Constants.colors["md_black_1000"])
                    .padding(.top, 8)
                    .padding(.bottom, 16)
                    .padding(.top, 16)
                
                Spacer()
            }
            
        } else {
            EmptyView()
        }
    }
    
    @ViewBuilder
    func renderCredits() -> some View {
        let credits = contentText.contentCredits
        
        if (credits != nil) {
            HStack {
                StyledTextView(
                    content: credits!,
                    fontName: "ProximaNova-Regular",
                    size: 12,
                    color: Constants.colors["md_grey_800"])
                    .padding(.top, 8)
                    .padding(.bottom, 16)
                    .padding(.top, 16)
                
                Spacer()
            }
            
        } else {
            EmptyView()
        }
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            renderTitle()
            renderText()
            renderCredits()
        }
        .padding(.horizontal, 16)
    }
}

struct ContentTextView_Previews: PreviewProvider {
    static var previews: some View {
        ContentTextView(contentText: Content.exampleText)
    }
}
