//
//  CustomImage.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 15/03/21.
//

import SwiftUI

struct CustomImage: View {
    var image: LocalImage?
    var type: PlaceholderType?
    var placeholderIconSize: CGFloat?
    
    func setupImageWithScale(_ imageComponent: Image, _ imageUrl: String, scaleType: String) -> some View {
        if scaleType == "center_crop" {
            return imageComponent
                .resizable()
                .aspectRatio(contentMode: .fill)
        } else if scaleType == "fit" {
            return imageComponent
                .resizable()
                .aspectRatio(contentMode: .fit)
        } else {
            return imageComponent
                .resizable()
                .aspectRatio(contentMode: .fill)
        }
    }
    
    var body: some View {
        
        if let image = image {
            let imageUrl = image.url ?? ""
            let imageComponent = Image(imageUrl)
            
            if let imageHeight = image.imageStyle?.resize?.height {
                if let imageScaleType = image.imageStyle?.scaleType {
                    setupImageWithScale(imageComponent, imageUrl, scaleType: imageScaleType)
                        .frame(height: CGFloat(imageHeight), alignment: .center)
                } else {
                    imageComponent
                        .frame(height: CGFloat(imageHeight), alignment: .center)
                }
            } else {
                if let imageScaleType = image.imageStyle?.scaleType {
                    setupImageWithScale(imageComponent, imageUrl, scaleType: imageScaleType)
                } else {
                    imageComponent
                }
            }
        } else {
            Placeholder(type: type ?? .movie, iconSize: placeholderIconSize)
        }
    }
}

struct CustomImage_Previews: PreviewProvider {
    static var previews: some View {
        CustomImage(image: LocalImage.example!)
    }
}
