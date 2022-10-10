//
//  GlossaryItemView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 10/10/22.
//

import SwiftUI

struct GlossaryItemView: View {
    let glossary: Glossary!
    
    var onClickLink: ((TextViewLinkScreen?) -> Void)? = nil
    var onClickListLink: ((TextViewLinkScreenType, Int?) -> Void)? = nil
    
    var body: some View {
        let color = ColorUtils.getRandomColorAssets().colorName
        
        VStack(alignment: .leading) {
            Text(glossary.name ?? "")
                .font(.oswaldBold(size: 22))
                .multilineTextAlignment(.leading)
                .foregroundColor(.textPrimary)
                .padding(.top, 10)
                .padding(.horizontal)
            
            RoundedRectangle(cornerRadius: 2, style: .continuous)
                .fill(Color(UIColor(colorName: "md_\(color)_500")))
                .frame(width: 150, height: 5)
                .padding(.leading)
                .padding(.bottom, 10)
            
            PageContentListView(
                contentList: glossary.contentList, onClickLink: onClickLink, onClickListLink: onClickListLink)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct GlossaryItemView_Previews: PreviewProvider {
    static var previews: some View {
        let glossary = Glossary.exampleGlossary()
        
        ScrollView {
            LazyVStack {
                GlossaryItemView(glossary: glossary)
            }
        }
    }
}
