//
//  NomineeListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 28/03/21.
//

import SwiftUI

struct NomineeListView: View {
    let nomineeList: [Nominee]
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(alignment: .top, spacing: 0) {
                ForEach(0 ..< nomineeList.count) { index in
                    let nominee = nomineeList[index]
                    
                    NomineeItemView(nominee: nominee)
                        .padding(.leading, index == 0 ? 16 : 8)
                }
            }
            .padding(.vertical, 16)
        }
    }
}

struct NomineeListView_Previews: PreviewProvider {
    static var previews: some View {
        let nominees = [
            Nominee.exampleNomineeMovie,
            Nominee.exampleNomineePerson,
            Nominee.exampleNomineeMovie,
            Nominee.exampleNomineeMovie
        ]
        
        NomineeListView(nomineeList: nominees)
    }
}
