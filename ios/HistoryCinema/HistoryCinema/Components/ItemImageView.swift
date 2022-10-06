//
//  ItemImageView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 22/03/21.
//

import SwiftUI
import Kingfisher

struct ItemImageView: View {
    let url: String
    var title: String? = nil
    var subtitle: String? = nil
    var imageWidth: Int? = nil
    var imageHeight: Int? = nil
    var placeholderType: PlaceholderType? = nil
    
    var body: some View {
        let width = imageWidth ?? 200
        let height = imageHeight ?? 280
        let type = placeholderType ?? .movie
            
        ZStack(alignment: Alignment(horizontal: .center, vertical: .bottom)) {
                
            CustomImage(
                imageUrl: url,
                imageType: .online,
                placeholderType: type,
                width: width,
                height: height,
                cornerRadius: 20
            )
                
            LinearGradient(
                gradient: Gradient(colors: [.black, .clear, .clear]),
                startPoint: .bottom,
                endPoint: .top
            )
            .cornerRadius(20)
            .frame(
                width: CGFloat(width),
                height: CGFloat(height)
            )
            
            VStack {
                if let movieTitle = self.title {
                    HStack {
                        Text(movieTitle)
                            .font(.oswaldBold(size: 18))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(Color.white)
                            .padding(.horizontal, 16)
                        
                        Spacer()
                    }
                    .frame(width: CGFloat(width))
                }
                
                if let movieSubtitle = self.subtitle {
                    HStack {
                        Text(movieSubtitle)
                            .font(.oswaldRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(Color.white)
                            .padding(.horizontal, 16)
                        
                        Spacer()
                    }
                    .frame(width: CGFloat(width))
                }
            }
            .padding(.bottom, 16)
        }
    }
}

struct ItemImageView_Previews: PreviewProvider {
    static var previews: some View {
        ItemImageView(
            url: "https://image.tmdb.org/t/p/w342//aaNIFWrq6eGi259APbB5yaqBFdm.jpg",
            title: "Teste de Title",
            subtitle: "Teste de Subtitulo"
        )
    }
}
