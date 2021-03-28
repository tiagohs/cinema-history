//
//  NomineeItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/03/21.
//

import SwiftUI

struct NomineeItemView: View {
    let nominee: Nominee
    
    @ViewBuilder
    func renderWinnerContainer(_ width: Int) -> some View {
        VStack {
            Text("Vencedor")
                .font(.oswaldBold(size: 14))
                .lineLimit(1)
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.white)
        }
        .frame(width: CGFloat(width), height: CGFloat(30))
        .clipped()
        .background(
            RoundedCorners(tl: 0, tr: 0, bl: 20, br: 20)
                        .fill(Color(UIColor.init(colorName: "oscar")))
        )
    }
    
    @ViewBuilder
    func renderItemHeader(_ width: Int, _ height: Int) -> some View {
        let imageUrl = ImageUtils.formatImageUrl(imageID: nominee.movie?.imagePath, imageSize: TMDB.ImageSize.POSTER.w185) ?? ""
        
        ZStack(alignment: Alignment(horizontal: .center, vertical: .top)) {
            LinearGradient(
                gradient: Gradient(colors: [.black, .clear, .clear]),
                startPoint: .top,
                endPoint: .bottom
            )
            .cornerRadius(20)
            .frame(
                width: CGFloat(width),
                height: 150
            )
            
            Button(action: {
                
            }) {
                HStack {
                    CustomImage(
                        imageUrl: imageUrl,
                        imageType: .online,
                        placeholderType: .movie,
                        width: 50,
                        height: 50,
                        iconSize: 30
                    )
                    .clipShape(Circle())
                    
                    VStack(alignment: .leading, spacing: 0) {
                        Text(nominee.movie?.name
                                ?? "")
                            .font(.oswaldBold(size: 14))
                            .lineLimit(1)
                            .multilineTextAlignment(.leading)
                            .foregroundColor(Color.white)
                            .padding(.horizontal, 16)
                        
                        Text(nominee.movie?.director ?? "")
                            .font(.oswaldBold(size: 12))
                            .lineLimit(1)
                            .multilineTextAlignment(.leading)
                            .foregroundColor(Color.white)
                            .padding(.horizontal, 16)
                    }
                    
                    Spacer()
                }
                .padding(.top, 8)
                .padding(.horizontal, 12)
                .frame(width: CGFloat(width), height: CGFloat(50))
            }
            .frame(width: CGFloat(width))
            .buttonStyle(PlainButtonStyle())
        }
        .frame(width: CGFloat(width))
    }
    
    @ViewBuilder
    func renderItemFooter(_ width: Int, _ height: Int) -> some View {
        ZStack(alignment: Alignment(horizontal: .center, vertical: .bottom)) {
            LinearGradient(
                gradient: Gradient(colors: [.black, .clear, .clear]),
                startPoint: .bottom,
                endPoint: .top
            )
            .cornerRadius(20)
            .frame(
                width: CGFloat(width),
                height: 150
            )
            
            VStack {
                renderItemTitle(width, height)
                renderItemDescription(nominee.type == .movie ? nominee.director : nominee.department)
                
                if nominee.winner == true {
                    renderWinnerContainer(width)
                }
            }
        }
        .frame(width: CGFloat(width))
    }
    
    @ViewBuilder
    func renderItemTitle(_ width: Int, _ height: Int) -> some View {
        HStack {
            Text(nominee.name ?? "")
                .font(.oswaldBold(size: 16))
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.white)
                .padding(.horizontal, 16)

            Spacer()
        }
    }
    
    @ViewBuilder
    func renderItemDescription(_ description: String?) -> some View {
        HStack {
            Text(description ?? "")
                .font(.proximaNovaRegular(size: 12))
                .multilineTextAlignment(.leading)
                .foregroundColor(Color(UIColor(colorName: "md_grey_100")))
                .padding(.horizontal, 16)
                .padding(.bottom, 5)
            
            Spacer()
        }
    }
    
    @ViewBuilder
    func renderMovieBody(_ width: Int, _ height: Int) -> some View {
        renderItemFooter(width, height)
    }
    
    @ViewBuilder
    func renderPersonBody(_ width: Int, _ height: Int) -> some View {
        VStack {
            renderItemHeader(width, height)
            
            Spacer()
            
            renderItemFooter(width, height)
        }
    }
    
    var body: some View {
        let width = 200
        let height = 300
        let type = nominee.type ?? .movie
        let imageUrl = ImageUtils.formatImageUrl(imageID: nominee.imagePath, imageSize: type == .movie ? TMDB.ImageSize.PROFILE.h632 : TMDB.ImageSize.POSTER.w780) ?? ""
        let placeholderType = type == .movie ? PlaceholderType.movie : PlaceholderType.person
        
        ZStack(alignment: Alignment(horizontal: .center, vertical: .bottom)) {
                
            CustomImage(
                imageUrl: imageUrl,
                imageType: .online,
                placeholderType: placeholderType,
                width: width,
                height: height,
                cornerRadius: 20
            )
            
            switch type {
            case .movie:
                renderMovieBody(width, height)
            case .person:
                renderPersonBody(width, height)
            }
        }
        .overlay(
                RoundedRectangle(cornerRadius: 20)
                    .stroke(Color(UIColor.init(colorName: "oscar")), lineWidth: 5)
            )
        .frame(width: CGFloat(width), height: CGFloat(height))
    }
}

struct NomineeItemView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            NomineeItemView(nominee: Nominee.exampleNomineeMovie)
            NomineeItemView(nominee: Nominee.exampleNomineePerson)
        }
    }
}
