//
//  ReferenceContentListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 07/10/22.
//

import SwiftUI

struct ReferenceContentListView: View {
    let references: [ReferenceContent]!
    
    @State private var pageLinkModel: PageLinkModel? = nil
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading) {
                ForEach(0 ..< references.count) { index in
                    let reference = references[index]
                    
                    if let referenceContentList = reference.references {
                        Text(reference.name ?? "")
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                            .padding(.vertical, 10)
                            .padding(.horizontal)
                        
                        ReferenceListView(references: referenceContentList) { textViewLinkScreen in
                            self.pageLinkModel = PageLinkModel(textViewLinkScreen?.id, textViewLinkScreen?.screenType)
                        }
                    }
                }
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

struct ReferenceContentListView_Previews: PreviewProvider {
    static var previews: some View {
        let referenceList = [
            Reference.example(.text),
            Reference.example(.media)
        ]
        
        ReferenceContentListView(references: [ReferenceContent.exampleReference(referenceList), ReferenceContent.exampleReference(referenceList)])
    }
}
