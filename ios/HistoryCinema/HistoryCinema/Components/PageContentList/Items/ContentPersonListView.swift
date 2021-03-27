//
//  ContentPersonListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 22/03/21.
//

import SwiftUI

struct ContentPersonListView: View {
    let contentPersonList: ContentPersonList
    
    var body: some View {
        ZStack {
            VStack(alignment: .leading) {
                HStack {
                    Text(contentPersonList.title ?? "Você deveria conhecer...")
                        .font(.oswaldRegular(size: 22))
                        .foregroundColor(Color(UIColor.init(colorName: "md_black_1000")))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(Color.white)
                        .padding(.horizontal, 16)
                        .padding(.bottom, 16)
                    
                    Spacer()
                }
                
                PersonListView(personList: contentPersonList.persons ?? [])
            }
            .frame(width: UIScreen.main.bounds.width)
            .padding(.vertical, 16)
        }
    }
}

struct ContentPersonListView_Previews: PreviewProvider {
    static var previews: some View {
        ContentPersonListView(contentPersonList: Content.examplePersonList)
    }
}
