//
//  PersonDetailsContentView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct PersonDetailsContentView: View {
    let person: Person!
    
    @State private var pageLinkModel: PageLinkModel? = nil

    var body: some View {
        ZStack(alignment: .top) {
            PersonDetailsHeaderView(person: person)
            
            VStack {
                PersonDetailsBiographyView(person: person)
                    .padding(.top, 490)
                
                PersonDetailsFilmographyView(person: person) { type, id  in
                    self.pageLinkModel = PageLinkModel(id, type)
                }
                    .padding(.vertical, 10)
                
                PersonDetailsMidiaView(person: person)
                    .padding(.vertical, 10)
            }
            .padding(.bottom, 10)
        }
        .sheet(item: self.$pageLinkModel) { item in
            MovieDetailsView(movieId: item.id!)
        }
    }
}

struct PersonDetailsContentView_Previews: PreviewProvider {
    static var previews: some View {
        ZStack(alignment: .topLeading) {
            ScrollView {
                VStack {
                    PersonDetailsContentView(person: Person.examplePersonFull)
                }
            }
            .edgesIgnoringSafeArea(.all)
        }
    }
}
