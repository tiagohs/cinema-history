//
//  MovieContentPersonListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct MovieContentPersonListView: View {
    let sectionName: String!
    let personList: [PersonItem]!
    
    @State private var pageLinkModel: PageLinkModel? = nil
    
    var body: some View {
        if let list = self.personList, !list.isEmpty {
            MovieContentContainer {
                VStack(alignment: .leading) {
                    Text(sectionName)
                        .font(.oswaldRegular(size: 24))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 5)
                        .padding(.leading, 20)
                    
                    ScrollView(.horizontal, showsIndicators: false) {
                        LazyHStack(alignment: .top, spacing: 0) {
                            ForEach(0 ..< list.count) { index in
                                let person = list[index]
                                let imageUrl = ImageUtils.formatImageUrl(imageID: person.pictureId, imageSize: TMDB.ImageSize.PROFILE.h632) ?? ""
                                
                                PersonItemView(imageUrl: imageUrl, personName: person.name,
                                               imageWidth: 150, imageHeight: 230)
                                    .padding(.leading, index == 0 ? 20 : 8)
                                    .onTapGesture {
                                        self.pageLinkModel = PageLinkModel(person.id, .person)
                                    }
                            }
                        }
                        .frame(height: 240)
                    }
                }
                .padding(.vertical, 20)
            }
            .sheet(item: $pageLinkModel) { pageLinkModel in
                PersonDetailsView(personId: pageLinkModel.id)
            }
        } else {
            AnyView(EmptyView())
        }
    }
}

struct MovieContentPersonListView_Previews: PreviewProvider {
    static var previews: some View {
        let personList = Movie.exampleMovieFull().credits?.cast?.map({ cast in
            return PersonItem(id: cast.id, name: cast.name, subtitle: cast.character, pictureId: cast.profilePath)
        })
        
        MovieContentPersonListView(sectionName: "Elenco", personList: personList)
    }
}
