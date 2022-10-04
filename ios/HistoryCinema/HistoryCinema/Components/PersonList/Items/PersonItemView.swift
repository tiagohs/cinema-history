//
//  PersonItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 22/03/21.
//

import SwiftUI

struct PersonItemView: View {
    let imageUrl: String?
    let personName: String?
    
    var imageWidth: Int? = nil
    var imageHeight: Int? = nil
    
    var body: some View {
        ItemImageView(
            url: imageUrl ?? "",
            title: personName,
            imageWidth: imageWidth,
            imageHeight: imageHeight,
            placeholderType: .person
        )
    }
}

struct PersonItemView_Previews: PreviewProvider {
    static var previews: some View {
        PersonItemView(imageUrl: "", personName: "teste")
    }
}
