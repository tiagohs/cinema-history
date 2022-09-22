//
//  SummaryItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 08/03/21.
//

import SwiftUI

struct SummaryItem: View {
    let summaryModel: SummaryModel
    
    var body: some View {
        VStack {
            HStack {
                Text(summaryModel.title)
                    .textCase(.uppercase)
                    .frame(maxWidth: .infinity)
                    .font(.proximaNovaBold(size: 20))
                    .multilineTextAlignment(.center)
                    .foregroundColor(Color.textPrimary)
                    .padding(.horizontal, 16)
            }
            
            Text(summaryModel.description)
                .frame(maxWidth: .infinity)
                .font(.proximaNovaRegular(size: 16))
                .multilineTextAlignment(.center)
                .foregroundColor(Color.textPrimary)
                .padding(.horizontal, 16)
                .padding(.top, 5)
        }
        .padding(.vertical, 22)
    }
}

struct SummaryItem_Previews: PreviewProvider {
    static var previews: some View {
        SummaryItem(summaryModel: SummaryModel.example)
    }
}
