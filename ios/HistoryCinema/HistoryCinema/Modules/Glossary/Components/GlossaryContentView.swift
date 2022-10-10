//
//  GlossaryContentView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 10/10/22.
//

import SwiftUI

struct GlossaryContentView: View {
    let glossaryList: [Glossary]!
    
    @State private var pageLinkModel: PageLinkModel? = nil
    
    @State var searchTerm = ""
    
    var body: some View {
        VStack {
            //SearchBarView(searchTerm: $searchTerm)
            
            if let glossaryListFilter = glossaryList
//                    .filter({ (glossary) -> Bool in
//                        self.searchTerm.isEmpty ? true :
//                        "\(String(describing: glossary.name))".lowercased().contains(self.searchTerm.lowercased() )})
                    .sorted { $0.name < $1.name }, !glossaryListFilter.isEmpty {
                ScrollView {
                    LazyVStack(alignment: .leading) {
                        ForEach(0 ..< glossaryListFilter.count) { index in
                            let glossary = glossaryListFilter[index]
                            
                            GlossaryItemView(glossary: glossary, onClickLink: { textViewLinkScreen in
                                self.pageLinkModel = PageLinkModel(textViewLinkScreen?.id, textViewLinkScreen?.screenType)
                            }) { textViewLinkScreen, id in
                                self.pageLinkModel = PageLinkModel(id, textViewLinkScreen)
                            }
                        }
                    }
                }
            } else {
                Text("Não encontrado. Tente buscar com um termo diferente.")
                    .font(.proximaNovaRegular(size: 16))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.textSecondary)
                    .frame(maxHeight: .infinity)
                    .padding(.vertical)
                    .padding(.horizontal)
            }
            
            
        }
        .sheet(item: self.$pageLinkModel) { item in
            if item.type == .movie {
                MovieDetailsView(movieId: item.id!)
            } else if item.type == .person {
                PersonDetailsView(personId: item.id)
            }
        }
    }
}

struct GlossaryContentView_Previews: PreviewProvider {
    static var previews: some View {
        let glossaryList = [
            Glossary.exampleGlossary(),
            Glossary.exampleGlossary()
        ]
        
        GlossaryContentView(glossaryList: glossaryList)
    }
}
