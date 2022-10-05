//
//  ImageListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct ImageListView: View {
    let images: [APIImage]
    
    @State var imageLinkModel: ImageLinkModel? = nil
    
    var body: some View {
        let imageDTO = images.map { apiImage in
            return ImageDTO(imageUrl: ImageUtils.formatImageUrl(imageID: apiImage.filePath, imageSize: TMDB.ImageSize.BACKDROP.w780) ?? "")
        }
        
        ScrollView(.horizontal, showsIndicators: false) {
            LazyHStack(alignment: .top, spacing: 0) {
                ForEach(0 ..< images.count) { index in
                    let image = images[index]
                    
                    ImageItemView(image: image, index: 0) { image, index in
                        let images = images.map { apiImage in
                            return ImageDTO(imageUrl: ImageUtils.formatImageUrl(imageID: apiImage.filePath, imageSize: TMDB.ImageSize.BACKDROP.w780) ?? "")
                        }
                        
                        self.imageLinkModel = ImageLinkModel(index, images)
                    }
                        .padding(.leading, index == 0 ? 20 : 8)
                }
            }
        }
        .sheet(item: $imageLinkModel) { imageLinkModel in
            ImageSlider(images: imageLinkModel.images, startIndex: imageLinkModel.index)
        }
    }
}

struct ImageListView_Previews: PreviewProvider {
    static var previews: some View {
        ImageListView(images: Movie.exampleMovieFull().allImages())
    }
}
