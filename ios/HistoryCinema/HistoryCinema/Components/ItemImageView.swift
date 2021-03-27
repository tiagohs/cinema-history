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
    var imageWidth: Int? = nil
    var imageHeight: Int? = nil
    var placeholderType: PlaceholderType? = nil
    
    var body: some View {
        let width = imageWidth ?? 200
        let height = imageHeight ?? 280
        let imageUrl = URL(string: url)
        let type = placeholderType ?? .movie
            
        ZStack(alignment: Alignment(horizontal: .center, vertical: .bottom)) {
                KFImage.url(imageUrl)
                    .placeholder {
                        Placeholder(type: type, iconSize: CGFloat(100))
                            .cornerRadius(20)
                            .frame(
                                width: CGFloat(width),
                                height: CGFloat(height)
                            )
                    }
                    .appendProcessor(
                        DownsamplingImageProcessor(
                            size: CGSize(width: width, height: height)))
                    .appendProcessor(RoundCornerImageProcessor(cornerRadius: 20))
                    .loadDiskFileSynchronously()
                    .resizable()
                    .cacheMemoryOnly()
                    .fade(duration: 0.25)
                    .frame(
                        width: CGFloat(width),
                        height: CGFloat(height)
                    )
                
                if title != nil {
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
                    
                    HStack {
                        Text(title!)
                            .font(.oswaldBold(size: 18))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(Color.white)
                            .padding(.horizontal, 16)
                            .padding(.bottom, 16)
                        
                        Spacer()
                    }
                    .frame(width: CGFloat(width))
                } else {
                    EmptyView()
                }
            }
    }
}

struct ItemImageView_Previews: PreviewProvider {
    static var previews: some View {
        ItemImageView(
            url: "https://image.tmdb.org/t/p/w342//aaNIFWrq6eGi259APbB5yaqBFdm.jpg",
            title: "Teste de Title"
        )
    }
}
