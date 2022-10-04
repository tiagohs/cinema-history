//
//  ImageItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct ImageItemView: View {
    let image: APIImage!
    let index: Int!
    let onImageClick: (APIImage, Int) -> Void
    
    var body: some View {
        let imageHeight = 170
        let url = ImageUtils.formatImageUrl(imageID: image.filePath, imageSize: TMDB.ImageSize.BACKDROP.w780) ?? ""
        
        SmoothAsyncImageView(url: URL(string: url)) { phase in
            if let image = phase.image {
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } else if phase.error != nil {
                Placeholder(type: .movie, iconSize: CGFloat(100))
                    .frame(
                        height: CGFloat(imageHeight)
                    )
            } else {
                Placeholder(type: .movie, iconSize: CGFloat(100))
                    .frame(
                        height: CGFloat(imageHeight)
                    )
            }
        }
        .frame(width: 260, height: CGFloat(imageHeight))
        .cornerRadius(0)
        .shadow(radius: 5)
        .overlay(
            RoundedRectangle(cornerRadius: 10)
                .stroke(Color(.sRGB, red: 150/255, green: 150/255, blue: 150/255, opacity: 0.1), lineWidth: 1)
        )
        .onTapGesture {
            onImageClick(image, index)
        }
}

struct ImageItemView_Previews: PreviewProvider {
    static var previews: some View {
        let image = Movie.exampleMovieFull().allImages()[0]
        
        ImageItemView(image: image, index: 0) { image, index in
            
        }
    }
    }
}
