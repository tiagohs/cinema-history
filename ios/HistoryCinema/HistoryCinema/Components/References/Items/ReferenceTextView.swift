//
//  ReferenceTextView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 07/10/22.
//

import SwiftUI

struct ReferenceTextView: View {
    let referenceText: ReferenceText!
    
    @Environment(\.colorScheme) var colorScheme
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    
    var body: some View {
        if let text = self.referenceText?.text {
            VStack(alignment: .leading) {
                ContentContainer {
                    HStack {
                        StyledTextView(
                            content: text,
                            fontName: "ProximaNova-Regular",
                            size: 16,
                            color: Color.textPrimaryHEX(colorScheme),
                            onClickLink: onClickLink
                        )
                            .fixedSize(horizontal: false, vertical: true)
                        Spacer()
                    }
                    .padding(20)
                }
            }
            .padding(.vertical, 4)
        } else {
            EmptyView()
        }
    }
}

struct ReferenceTextView_Previews: PreviewProvider {
    static var previews: some View {
        ReferenceTextView(referenceText: (Reference.example(.text) as! ReferenceText))
    }
}
