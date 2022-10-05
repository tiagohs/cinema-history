//
//  PersonListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 22/03/21.
//

import SwiftUI

struct PersonListView: View {
    let personList: [LocalPerson]
    var imageWidth: Int? = nil
    var imageHeight: Int? = nil
    
    var onClickLink: ((TextViewLinkScreenType, Int?) -> Void)? = nil
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .top, spacing: 0) {
                ForEach(0 ..< personList.count) { index in
                    let person = personList[index]
                    let imageUrl = ImageUtils.formatImageUrl(imageID: person.profilePath, imageSize: TMDB.ImageSize.PROFILE.h632) ?? ""
                    
                    PersonItemView(imageUrl: imageUrl, personName: person.name,
                                   imageWidth: 230, imageHeight: 350)
                        .padding(.leading, index == 0 ? 16 : 8)
                        .onTapGesture {
                            onClickLink?(TextViewLinkScreenType.person, person.id)
                        }
                }
            }
        }
    }
}

struct PersonListView_Previews: PreviewProvider {
    static var previews: some View {
        let personList = [
            LocalPerson.example,
            LocalPerson.example,
            LocalPerson.example,
            LocalPerson.example
        ]
        
        PersonListView(personList: personList)
    }
}
