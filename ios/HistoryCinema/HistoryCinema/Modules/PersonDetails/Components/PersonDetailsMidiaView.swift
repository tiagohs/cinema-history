//
//  PersonDetailsMidiaView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 06/10/22.
//

import SwiftUI

struct PersonDetailsMidiaView: View {
    let person: Person!
    
    var body: some View {
        let allImages = person.allImages()
        
        if !allImages.isEmpty {
            ContentContainer {
                VStack(alignment: .leading) {
                    Text("Mídia")
                        .font(.oswaldRegular(size: 24))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 5)
                        .padding(.leading, 20)
                    
                    ImageListView(images: allImages)
                }
                .padding(.leading, 12)
                .padding(.vertical, 20)
            }
        }
    }
}

struct PersonDetailsMidiaView_Previews: PreviewProvider {
    static var previews: some View {
        PersonDetailsMidiaView(person: Person.examplePersonFull)
    }
}
