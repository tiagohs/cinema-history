//
//  ContentImageView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/03/21.
//

import SwiftUI

struct ContentImageView: View {
    let contentImage: ContentImage
    
    var body: some View {
        let imageUrl = contentImage.image.url ?? ""
        let height = contentImage.height ?? 180
        
        VStack {
            Image(imageUrl)
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(width: UIScreen.main.bounds.width, height: CGFloat(height))
                .clipped()
            
            ContentInformationView(contentInformation: contentImage.information)
        }
        .padding(.vertical, 16)
        .fixedSize(horizontal: false, vertical: true)
    }
}

struct ContentImageView_Previews: PreviewProvider {
    static var previews: some View {
        ContentImageView(contentImage: Content.exampleImage)
    }
}
