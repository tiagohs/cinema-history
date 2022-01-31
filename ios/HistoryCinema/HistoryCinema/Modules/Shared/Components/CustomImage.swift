//
//  CustomImage.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 15/03/21.
//

import SwiftUI
import Kingfisher

struct CustomImage: View {
    var image: LocalImage?
    var imageUrl: String?
    var imageType: ImageType?
    var placeholderType: PlaceholderType?
    var placeholderIconSize: CGFloat?
    var width: Int?
    var height: Int?
    var cornerRadius: Int?
    var blurRadius: Int?
    var iconSize: Int?
    
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
    
    @ViewBuilder
    func getOnlineImage() -> some View {
        let url = imageUrl ?? ""
        let type = placeholderType ?? .movie
        let imageWidth = (width != nil) ? CGFloat(width!) : UIScreen.main.bounds.width
        let height = height ?? 300
        let cornerRadius = cornerRadius ?? 0
        
        
        let imageComponent = KFImage.url(URL(string: url))
            .placeholder {
                Placeholder(type: type, iconSize: CGFloat(iconSize ?? 100))
                    .cornerRadius(CGFloat(cornerRadius))
                    .frame(
                        width: imageWidth,
                        height: CGFloat(height)
                    )
            }
            .appendProcessor(
                DownsamplingImageProcessor(
                    size: CGSize(width: (width != nil) ? Int(width!) : Int(UIScreen.main.bounds.width), height: height)))
            .appendProcessor(RoundCornerImageProcessor(cornerRadius: CGFloat(cornerRadius)))
            .appendProcessor(CroppingImageProcessor(size: CGSize(width: (width != nil) ? Int(width!) : Int(UIScreen.main.bounds.width), height: height)))
            .loadDiskFileSynchronously()
            .resizable()
            .cacheMemoryOnly()
            .fade(duration: 0.25)
        
        if let blurRadius = blurRadius {
            imageComponent
                .appendProcessor(BlurImageProcessor(blurRadius: CGFloat(blurRadius)))
                .scaledToFill()
                .frame(
                    width: imageWidth,
                    height: CGFloat(height)
                )
        } else {
            imageComponent
                .scaledToFill()
                .frame(
                    width: imageWidth,
                    height: CGFloat(height)
                )
        }
    }
    
    @ViewBuilder
    func getLocalImage() -> some View {
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
                if height != nil {
                    if let imageScaleType = image.imageStyle?.scaleType {
                        setupImageWithScale(imageComponent, imageUrl, scaleType: imageScaleType)
                            .frame(height: CGFloat(height!), alignment: .center)
                    } else {
                        imageComponent
                            .frame(height: CGFloat(height!), alignment: .center)
                    }
                } else {
                    if let imageScaleType = image.imageStyle?.scaleType {
                        setupImageWithScale(imageComponent, imageUrl, scaleType: imageScaleType)
                    } else {
                        imageComponent
                    }
                }
            }
        } else {
            Placeholder(type: placeholderType ?? .movie, iconSize: placeholderIconSize)
        }
    }
    
    var body: some View {
        switch imageType {
        case .online:
            getOnlineImage()
        case .local:
            getLocalImage()
        default:
            getLocalImage()
        }
    }
}

struct CustomImage_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            CustomImage(image: LocalImage.example!)
            CustomImage(imageUrl: "https://image.tmdb.org/t/p/h632//ba3Kfc01Dbigt41lyuFoZR7gmv1.jpg", imageType: .online)
        }
    }
}
