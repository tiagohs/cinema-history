//
//  PersonDetailsBiographyView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 05/10/22.
//

import SwiftUI

struct PersonDetailsBiographyView: View {
    let person: Person!
    
    var body: some View {
        if let biography = person.biographyFull() {
            ContentContainer {
                VStack(alignment: .leading) {
                    Text("Biografia")
                        .font(.oswaldRegular(size: 24))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 5)
                    
                    HStack {
                        Text(biography)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }.fixedSize(horizontal: false, vertical: true)
                    
                    Text("Fonte: TMDB")
                        .font(.oswaldRegular(size: 12))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textSecondary)
                }
                .padding(20)
            }
        } else {
            AnyView(EmptyView())
        }
    }
}

struct PersonDetailsBiographyView_Previews: PreviewProvider {
    static var previews: some View {
        PersonDetailsBiographyView(person: Person.examplePersonFull)
    }
}
