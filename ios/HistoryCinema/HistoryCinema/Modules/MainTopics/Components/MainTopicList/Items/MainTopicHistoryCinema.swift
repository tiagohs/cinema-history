//
//  MainTopicHistoryCinema.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicHistoryCinema: View {
    let mainTopicItem: MainTopicItem
    
    var body: some View {
        let image = mainTopicItem.image
        
        VStack {
            if let imageUrl = image?.url {
                let imageHeight = CGFloat(image?.imageStyle?.resize?.height ?? 400)
                let imageComponent = Image(imageUrl)
                
                if let imageScaleType = image?.imageStyle?.scaleType {
                    if imageScaleType == "center_crop" {
                        imageComponent
                            .resizable()
                            .scaledToFill()
                            .frame(height: imageHeight)
                    } else {
                        imageComponent
                            .resizable()
                            .scaledToFill()
                            .frame(height: imageHeight)
                    }
                } else {
                    imageComponent
                        .frame(height: imageHeight)
                }
            }
            
            VStack {
                Text(mainTopicItem.subtitle)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .font(.oswaldLight(size: 16))
                    .foregroundColor(Color(UIColor(hex: "#7D7E80")))
                
                Text(mainTopicItem.title)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .font(.oswaldBold(size: 28))
                    .foregroundColor(Color.white)
                
                Text(mainTopicItem.description)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .font(.proximaNovaRegular(size: 14))
                    .foregroundColor(Color.white)
                    .padding(.top, 5)
                    .padding(.bottom, 16)
            }
            .padding(.horizontal, 16)
        }
        .frame(
            width: UIScreen.main.bounds.width - 32)
        .background(Color.black)
        .cornerRadius(25)
    }
}

struct MainTopicHistoryCinema_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.history_cinema)
        
        MainTopicHistoryCinema(mainTopicItem: mainTopic as! MainTopicItem)
    }
}
