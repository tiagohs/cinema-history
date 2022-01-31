//
//  ContentItemHeader.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/03/21.
//

import SwiftUI

struct ContentInformationView: View {
    let contentInformation: ContentInformation
    
    @ViewBuilder
    func renderTitle() -> some View {
        let title = contentInformation.contentTitle
        
        if (title != nil) {
            HStack {
                Text(title!)
                    .font(.proximaNovaBold(size: 16))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(Color(UIColor(colorName: "md_grey_700")))
                    .padding(.horizontal, 16)
                    .padding(.top, 5)
                
                Spacer()
            }
        } else {
            EmptyView()
        }
    }
    
    @ViewBuilder
    func renderText() -> some View {
        let text = contentInformation.contentText
        
        if (text != nil) {
            HStack {
                StyledTextView(
                    content: text!,
                    fontName: "ProximaNova-Regular",
                    size: 14,
                    color: Constants.colors["md_grey_700"])
                    .fixedSize(horizontal: false, vertical: true)
                    .padding(.horizontal, 16)
                    .padding(.top, 5)
                
                Spacer()
            }
        } else {
            EmptyView()
        }
    }
    
    @ViewBuilder
    func renderReference() -> some View {
        let reference = contentInformation.source
        
        if (reference != nil) {
            HStack {
                StyledTextView(
                    content: reference!,
                    fontName: "ProximaNova-Regular",
                    size: 12,
                    color: Constants.colors["md_grey_900"])
                    .padding(.horizontal, 16)
                    .padding(.top, 5)
                
                Spacer()
            }
        } else {
            EmptyView()
        }
    }
    
    var body: some View {
        VStack(spacing: 0) {
            renderTitle()
            renderText()
            renderReference()
        }
    }
}

struct ContentItemHeader_Previews: PreviewProvider {
    static var previews: some View {
        ContentInformationView(contentInformation: Content.exampleGif.information)
    }
}
