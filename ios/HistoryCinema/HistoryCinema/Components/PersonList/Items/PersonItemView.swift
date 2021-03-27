//
//  PersonItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 22/03/21.
//

import SwiftUI

struct PersonItemView: View {
    let person: LocalPerson
    var imageWidth: Int? = nil
    var imageHeight: Int? = nil
    
    var body: some View {
        let imageUrl = ImageUtils.formatImageUrl(imageID: person.profilePath, imageSize: TMDB.ImageSize.PROFILE.h632) ?? ""
        
        ItemImageView(
            url: imageUrl,
            title: person.name, 
            imageWidth: imageWidth,
            imageHeight: imageHeight,
            placeholderType: .person
        )
    }
}

struct PersonItemView_Previews: PreviewProvider {
    static var previews: some View {
        PersonItemView(person: LocalPerson.example)
    }
}
