//
//  ImageSlider.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI
import Combine

struct ImageSliderView: View {
    let images: [ImageDTO]!
    var startIndex: Int = 0
    
    @State private var selected = 0
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    init(images: [ImageDTO], startIndex: Int = 0) {
        self.images = images
        self.startIndex = startIndex
        self.selected = startIndex
    }
    
    var body: some View {
        let imageHeight = 200
        
        ZStack(alignment: .topLeading) {
            VStack {
                TabView(selection: self.$selected) {
                    ForEach(images.indices, id: \.self) { index in
                        let item = images[index]
                        
                         //3
                        SmoothAsyncImageView(url: URL(string: item.imageUrl)) { phase in
                            if let image = phase.image {
                                image
                                    .resizable()
                                    .aspectRatio(contentMode: .fill)
                                    .frame(height: CGFloat(imageHeight))
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
                        .tag(index)
                    }
                }
                .animation(.easeOut)
                .tabViewStyle(PageTabViewStyle())
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(Color.black)
            
            Button(action: {
                self.presentationMode.wrappedValue.dismiss()
            }) {
                Image(systemName: "xmark")
                    .font(.system(size: 16, weight: .bold))
                    .padding()
                    .background(Color.backgroundPrimary)
                    .foregroundColor(Color.textPrimary)
                    .clipShape(Circle())
            }
            .shadow(color: Color.backgroundPrimary, radius: 5)
            .padding()
        }
    }
}

struct ImageSliderView_Previews: PreviewProvider {
    static var previews: some View {
        let images = Movie.exampleMovieFull().allImages().map { apiImage in
            return ImageDTO(imageUrl: ImageUtils.formatImageUrl(imageID: apiImage.filePath, imageSize: TMDB.ImageSize.BACKDROP.w780) ?? "")
        }
        
        ImageSliderView(images: images, startIndex: 2)
    }
}
