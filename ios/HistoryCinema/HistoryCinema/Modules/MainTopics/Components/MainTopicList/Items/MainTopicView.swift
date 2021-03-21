//
//  MainTopicView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicContent<Content : View>: View {
    var layoutType: MainTopicItemLayoutType!
    var bodyContent: () -> Content
    
    @inlinable public init(layoutType: MainTopicItemLayoutType!, @ViewBuilder content: @escaping () -> Content) {
        self.layoutType = layoutType
        self.bodyContent = content
    }
    
    var body: some View {
        if layoutType == .card_full {
            ZStack(alignment: .bottomLeading) {
                bodyContent()
            }
        } else {
            VStack {
                bodyContent()
            }
        }
    }
}

struct MainTopicView: View {
    var subtitle: String?
    var title: String?
    var description: String?
    var image: LocalImage!
    var layoutType: MainTopicItemLayoutType!
    var titleColor: String?
    var titleBackgroundColor: String?
    
    var body: some View {
        let textColor = (titleColor != nil) ? Color(UIColor(colorName: titleColor!)) : Color.white
        let backgroundColor = (titleBackgroundColor != nil) ? Color(UIColor(colorName: titleBackgroundColor!)) : Color.black
        
        let mainTopicViewContainer = MainTopicContent(layoutType: layoutType) {
            CustomImage(image: image)
            
            let textContainer = VStack {
                if subtitle != nil {
                    Text(subtitle!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.oswaldLight(size: 16))
                        .foregroundColor(textColor)
                        .padding(.horizontal, 16)
                }
                
                if title != nil {
                    Text(title!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.oswaldBold(size: 28))
                        .foregroundColor(textColor)
                        .padding(.horizontal, 16)
                }
                
                if description != nil {
                    Text(description!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.proximaNovaRegular(size: 14))
                        .foregroundColor(textColor)
                        .padding(.top, 5)
                        .padding(.horizontal, 16)
                }
            }
            .padding(.vertical, 16)
            
            if titleBackgroundColor != "transparent" {
                textContainer
                    .background(backgroundColor)
            } else {
                textContainer
            }
            
        }
        
        
        if (layoutType != .full) {
            if titleBackgroundColor != "transparent" {
                mainTopicViewContainer
                    .background(backgroundColor)
                    .frame(width: UIScreen.main.bounds.width - 32, alignment: .center)
                    .cornerRadius(25)
            } else {
                mainTopicViewContainer
                                .frame(width: UIScreen.main.bounds.width - 32, alignment: .center)
                                .cornerRadius(25)
            }
        } else {
            mainTopicViewContainer
        }
    }
}

struct MainTopicView_Previews: PreviewProvider {
    static var previews: some View {
        let image = LocalImage(JSONString: "{\n            \"image_type\": \"local\",\n            \"url\": \"img_exorcist\",\n            \"content_description\": \"Foto da personagem Regan possúida pelo demônio, em O Exorcista.\",\n            \"animation\": {\n              \"type\": \"blink\",\n              \"duration\": 1000\n            },\n            \"style\": {\n              \"scale_type\": \"center_crop\",\n              \"resize\": {\n                \"height\": 250\n              }\n            }\n          }")
        
        Group {
            MainTopicView(
                subtitle: "Parte 01",
                title: "De 1895 a 1929",
                description: "Os visionários, inventores, sonhadores. As experimentações e descobertas de Georges Mélies, o humor único de Chaplin e Keaton, o surrealismo na Alemanha, a luta por uma voz com os Race Films, o romantismo e o espetáculo de Hollywood dos anos 20.",
                image: image,
                layoutType: MainTopicItemLayoutType.card
            )
            MainTopicView(
                title: "De 1895 a 1929",
                image: image, layoutType: MainTopicItemLayoutType.full)
            MainTopicView(
                subtitle: "Parte 01",
                title: "De 1895 a 1929",
                description: "Os visionários, inventores, sonhadores. As experimentações e descobertas de Georges Mélies, o humor único de Chaplin e Keaton, o surrealismo na Alemanha, a luta por uma voz com os Race Films, o romantismo e o espetáculo de Hollywood dos anos 20.",
                image: image,
                layoutType: MainTopicItemLayoutType.card_full,
                titleBackgroundColor: "transparent")
        }
        
        
    }
}
