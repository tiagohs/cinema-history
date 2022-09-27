//
//  MilMoviesHeader.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 26/09/22.
//

import SwiftUI

struct MilMoviesHeader: View {
    let mainTopic: MilMoviesMainTopic
    
    var body: some View {
        let colorAsset = ColorUtils.getRandomColorAssets()
        let textColor = Color(UIColor(colorName: colorAsset.textColorName))
        let backgroundColor = Color(UIColor(colorName: "md_\(colorAsset.colorName)_500"))
        
        Section.init {
            ZStack(alignment: .bottom) {
                Image((mainTopic.image?.url)!)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .blur(radius: 4, opaque: false)
                    .frame(height: 250)
                    .clipped()
                
                Rectangle()
                    .background(Color.black)
                    .blur(radius: 8, opaque: false)
                    .frame(height: 250)
                    .opacity(0.3)
                
                VStack {
                    Text("1001 Filmes para ver antes de morrer")
                        .font(.proximaNovaBold(size: 18))
                        .multilineTextAlignment(.center)
                        .foregroundColor(Color.white)
                        .padding(.bottom, 2)
                    
                    Text(mainTopic.title!)
                        .font(.oswaldBold(size: 22))
                        .multilineTextAlignment(.center)
                        .foregroundColor(textColor)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(backgroundColor)
                        .cornerRadius(12)
                }
                    .padding(.horizontal)
                    .padding(.bottom, 28)
            }
        }
    }
}

struct MilMoviesHeader_Previews: PreviewProvider {
    static var previews: some View {
        let milMoviesMainTopic = MainTopic.example(.mil_movies) as! MilMoviesMainTopic
        
        MilMoviesHeader(mainTopic: milMoviesMainTopic)
    }
}
