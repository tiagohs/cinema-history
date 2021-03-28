//
//  ContentNomineesView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/03/21.
//

import SwiftUI

struct ContentNomineesView: View {
    let contentNominee: ContentNominee!
    
    var body: some View {
        ZStack {
            VStack(alignment: .leading, spacing: 0) {
                HStack {
                    Text(contentNominee.name ?? "Best Picture")
                        .font(.oswaldRegular(size: 22))
                        .foregroundColor(Color(UIColor.init(colorName: "oscar")))
                        .multilineTextAlignment(.leading)
                        .padding(.horizontal, 16)
                    
                    Spacer()
                }
                
                Rectangle()
                    .fill(Color(UIColor(colorName: "oscar")))
                    .frame(width: 80, height: 2)
                    .padding(.horizontal, 16)
                
                NomineeListView(nomineeList: contentNominee.nomineeList ?? [])
            }
            .frame(width: UIScreen.main.bounds.width)
            .padding(.vertical, 16)
        }
    }
}

struct ContentNomineesView_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            ContentNomineesView(contentNominee: Content.exampleNomineesMovies)
            ContentNomineesView(contentNominee: Content.exampleNomineesPersons)
        }
    }
}
