//
//  ReferenceMediaView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 07/10/22.
//

import SwiftUI

struct ReferenceMediaView: View {
    let referenceMedia: ReferenceMedia!
    
    var body: some View {
        let colorAsset = ColorUtils.getReferenceColorAsset(referenceMedia.mediaType)
        let textColor = Color(UIColor(colorName: colorAsset.textColorName))
        let backgroundColor = Color(UIColor(colorName: colorAsset.colorName))
        let imageUrl = referenceMedia.image.url
        
        ZStack(alignment: Alignment(horizontal: .leading, vertical: .top)) {
            ContentContainer {
                Text(referenceMedia.title)
                    .font(.oswaldBold(size: 16))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.textPrimary)
                    .padding(.leading, 150)
                    .padding(.trailing, 90)
                    .padding(.top, 5)
                
                Text(referenceMedia.subtitle)
                    .font(.proximaNovaRegular(size: 12))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.textSecondary)
                    .padding(.top, 2)
                    .padding(.leading, 150)
                    .padding(.trailing, 10)
                
                Text(referenceMedia.description)
                    .font(.proximaNovaRegular(size: 14))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.textPrimary)
                    .padding(.leading, 150)
                    .padding(.trailing, 10)
                    .padding(.top, 5)
                    .padding(.bottom, 10)
            }
            .padding(.top, 20)
            
            HStack(alignment: .top) {
                ZStack(alignment: .bottom) {
                    CustomImage(
                        imageUrl: imageUrl,
                        imageType: .online,
                        placeholderType: .movie,
                        width: 120,
                        height: 160
                    )
                    
                    Text(referenceMedia.mediaType)
                        .font(.oswaldBold(size: 14))
                        .multilineTextAlignment(.leading)
                        .frame(width: 120)
                        .foregroundColor(textColor)
                        .background(backgroundColor)
                }
                .cornerRadius(12)
                .frame(width: 120, height: 160)
                
                Spacer()
                
                Button(action: {
                    if let url = URL(string: referenceMedia.link) {
                       UIApplication.shared.open(url)
                    }
                }) {
                    Text("Comprar")
                        .font(.proximaNovaRegular(size: 14))
                        .padding(.horizontal, 12)
                        .padding(.vertical, 10)
                        .background(backgroundColor)
                        .foregroundColor(textColor)
                        .cornerRadius(5)
                }
                .buttonStyle(PlainButtonStyle())
            }
            .padding(.leading, 30)
            .padding(.trailing, 20)
            .padding(.top, 10)
        }
    }
}

struct ReferenceMediaView_Previews: PreviewProvider {
    static var previews: some View {
        ReferenceMediaView(referenceMedia: Reference.example(.media) as! ReferenceMedia)
    }
}
