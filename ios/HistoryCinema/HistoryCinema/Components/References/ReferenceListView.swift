//
//  ReferenceListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 07/10/22.
//

import SwiftUI

struct ReferenceListView: View {
    let references: [Reference]!
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    
    var body: some View {
        ForEach(0 ..< references.count) { index in
            let referenceItem = references[index]
            
            switch referenceItem.type {
            case .text:
                ReferenceTextView(referenceText: (referenceItem as! ReferenceText), onClickLink: onClickLink)
                    .fixedSize(horizontal: false, vertical: true)
            case .media:
                ReferenceMediaView(referenceMedia: (referenceItem as! ReferenceMedia))
            default:
                EmptyView()
            }
        }
    }
}

struct ReferenceListView_Previews: PreviewProvider {
    static var previews: some View {
        let referenceList = [
            Reference.example(.text),
            Reference.example(.media)
        ]
        
        VStack {
            ReferenceListView(references: referenceList)
        }
    }
}
