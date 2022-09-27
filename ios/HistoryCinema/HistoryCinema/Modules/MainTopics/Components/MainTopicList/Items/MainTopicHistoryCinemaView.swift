//
//  MainTopicHistoryCinema.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicHistoryCinemaView: View {
    let mainTopicItem: MainTopicItem
    
    var body: some View {
        let subtitle = mainTopicItem.subtitle
        let title = mainTopicItem.title
        let description = mainTopicItem.description
        let titleColor = mainTopicItem.titleColor
        let titleBackgroundColor = mainTopicItem.titleBackgroundColor
        let image = mainTopicItem.image
        let textColor = (titleColor != nil) ? Color(UIColor(colorName: titleColor!)) : Color.white
        let backgroundColor = (titleBackgroundColor != nil) ? Color(UIColor(colorName: titleBackgroundColor!)) : Color.black
        let imageHeight = (image?.imageStyle?.height != nil) ? image!.imageStyle!.height! : 250
        
        VStack {
            Image(image!.url!)
                .resizable()
                .frame(height: CGFloat(imageHeight))
            
            VStack(alignment: .leading) {
                if subtitle != nil {
                    Text(subtitle!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.oswaldLight(size: 16))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(textColor)
                }

                if title != nil {
                    Text(title!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.oswaldBold(size: 28))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(textColor)
                }

                if description != nil {
                    Text(description!)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .font(.proximaNovaRegular(size: 14))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(textColor)
                        .padding(.top, 5)
                }
            }
            .padding()
            .background(backgroundColor)
        }
        .background(backgroundColor)
        .cornerRadius(25)
        .shadow(radius: 5)
        .overlay(
            RoundedRectangle(cornerRadius: 25)
                .stroke(Color(.sRGB, red: 150/255, green: 150/255, blue: 150/255, opacity: 0.1), lineWidth: 1)
        )
        .padding()
    }
}

struct MainTopicHistoryCinema_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.history_cinema)
        
        MainTopicHistoryCinemaView(mainTopicItem: mainTopic as! MainTopicItem)
    }
}
