//
//  ImageSlider.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct ImageSlider: View {
    let images: [ImageDTO]!
    var startIndex: Int = 0
    
    @State private var selected = 0
    
    init(images: [ImageDTO], startIndex: Int = 0) {
        self.images = images
        self.startIndex = startIndex
        self.selected = startIndex
    }
    
    var body: some View {
        let imageHeight = 200
        
        VStack {
            TabView(selection: $selected) {
                ForEach(images, id: \.self) { item in
                     //3
                    SmoothAsyncImageView(url: URL(string: item.imageUrl)) { phase in
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
                    .frame(height: CGFloat(imageHeight))
                }
            }
            
            .tabViewStyle(PageTabViewStyle())
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.black)
        
        
    }
}

struct ImageSlider_Previews: PreviewProvider {
    static var previews: some View {
        let images = Movie.exampleMovieFull().allImages().map { apiImage in
            return ImageDTO(imageUrl: ImageUtils.formatImageUrl(imageID: apiImage.filePath, imageSize: TMDB.ImageSize.BACKDROP.w780) ?? "")
        }
        
        ImageSlider(images: images, startIndex: 2)
    }
}
