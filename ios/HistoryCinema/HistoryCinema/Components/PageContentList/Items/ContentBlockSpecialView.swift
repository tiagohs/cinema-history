//
//  ContentBlockSpecialView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/03/21.
//

import SwiftUI

struct ContentBlockSpecialView: View {
    let contentBlockSpecial: ContentBlockSpecial
    
    var body: some View {
        let colorAsset = ColorUtils.getRandomColorAssets()
        let backgroundColor = Color(UIColor(colorName: "md_\(colorAsset.colorName)_500"))
        let textColor = Constants.colors["md_\(colorAsset.textColorName)_900"]
        
        VStack {
            ZStack {
                ZStack {
                    VStack(alignment: .leading, spacing: 0) {
                        HStack {
                            StyledTextView(
                                content: contentBlockSpecial.title ?? "",
                                fontName: "Oswald-Bold",
                                size: 18,
                                color: textColor)
                                .fixedSize(horizontal: false, vertical: true)
                                .padding(.bottom, 8)
                                .padding(.horizontal, 16)
                            
                            Spacer()
                        }
                        .padding(.horizontal, 16)
                        
                        HStack {
                            StyledTextView(
                                content: contentBlockSpecial.description ?? "",
                                fontName: "ProximaNova-Regular",
                                size: 14,
                                color: textColor
                            )
                                .fixedSize(horizontal: false, vertical: true)
                                .multilineTextAlignment(.leading)
                                .padding(.bottom, 8)
                                .padding(.horizontal, 16)
                            
                            Spacer()
                        }
                        .padding(.horizontal, 16)
                        
                        HStack {
                            StyledTextView(
                                content: contentBlockSpecial.credits ?? "",
                                fontName: "ProximaNova-RegularItalic",
                                size: 12,
                                color: textColor)
                                .fixedSize(horizontal: false, vertical: true)
                                .padding(.bottom, 8)
                                .padding(.leading, 16)
                                .multilineTextAlignment(.leading)
                            
                            Spacer(minLength: 10)
                            
                            Text("Clique aqui")
                                .font(.proximaNovaBold(size: 13))
                                .foregroundColor(Color(UIColor(colorName: "md_\(colorAsset.colorName)_900")))
                                .multilineTextAlignment(.leading)
                                .foregroundColor(Color.white)
                                .padding(.trailing, 16)
                        }
                        .padding(.bottom, 12)
                        .padding(.horizontal, 16)
                        
                        DividerColorView(colorName: colorAsset.colorName)
                        
                        CustomImage(
                            image: contentBlockSpecial.image,
                            placeholderType: .movie,
                            height: 200)
                    }
                    .frame(width: UIScreen.main.bounds.width)
                }
                .padding(.top, 12)
                .background(backgroundColor)
            }
            .frame(
                width: UIScreen.main.bounds.width - 32)
            .cornerRadius(10)
        }
        .padding(.vertical, 16)
    }
}

struct ContentBlockSpecialView_Previews: PreviewProvider {
    static var previews: some View {
        ContentBlockSpecialView(contentBlockSpecial: Content.exampleBlockSpecial)
    }
}
