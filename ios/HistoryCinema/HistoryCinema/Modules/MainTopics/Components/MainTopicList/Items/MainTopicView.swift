//
//  MainTopicView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicView: View {
    var subtitle: String?
    var title: String?
    var description: String?
    var image: LocalImage!
    var layoutType: MainTopicItemLayoutType!
    var titleColor: String?
    var titleBackgroundColor: String?
    
    var body: some View {
        let mainTopicViewContainer = VStack {
            Image.load(from: image)
            
            VStack {
                if subtitle != nil {
                    Text(subtitle!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.oswaldLight(size: 16))
                        .foregroundColor((titleColor != nil) ? Color(UIColor(hex: titleColor!)) : Color(UIColor(hex: "#7D7E80")))
                }
                
                if title != nil {
                    Text(title!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.oswaldBold(size: 28))
                        .foregroundColor((titleColor != nil) ? Color(UIColor(hex: titleColor!)) : Color.white)
                }
                
                if description != nil {
                    Text(description!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.proximaNovaRegular(size: 14))
                        .foregroundColor((titleColor != nil) ? Color(UIColor(hex: titleColor!)) : Color.white)
                        .padding(.top, 5)
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
        }
        
        if (layoutType == .full) {
            mainTopicViewContainer
                            .frame(width: UIScreen.main.bounds.width)
                            .background((titleBackgroundColor != nil) ? Color(UIColor(hex: titleBackgroundColor!)) : Color.black)
        } else {
            mainTopicViewContainer
                            .frame(width: UIScreen.main.bounds.width - 32)
                            .background(Color.black)
                            .cornerRadius(25)
        }
    }
}

struct MainTopicView_Previews: PreviewProvider {
    static var previews: some View {
        let image = LocalImage(JSONString: "{\n      \"image_type\": \"local\",\n      \"url\": \"img_voyage\",\n      \"content_description\": \"Cena do filme Viagem a Lua, onde uma lua, com feições humanas, é atingida no olho direito por um foguete.\",\n      \"animation\": {\n        \"type\": \"shake_vertical\",\n        \"duration\": 1300\n      },\n      \"style\": {\n        \"scale_type\": \"center_inside\",\n        \"resize\": {\n          \"height\": 250\n        }\n      }\n    }")
        
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
                image: image, layoutType: MainTopicItemLayoutType.card_full)
        }
        
        
    }
}
