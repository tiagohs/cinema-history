//
//  ContentMovieListView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 21/03/21.
//

import SwiftUI

struct ContentMovieListView: View {
    let contentMovieList: ContentMovieList
    
    var body: some View {
        let colorAsset = ColorUtils.getRandomColorAssets()
        let textColor = Color(UIColor(colorName: colorAsset.textColorName))
        let backgroundColor = Color(UIColor(colorName: "md_\(colorAsset.colorName)_500"))
        
        ZStack {
            VStack(alignment: .leading) {
                HStack {
                    Text("Vale a Conferida")
                        .font(.oswaldRegular(size: 22))
                        .foregroundColor(textColor)
                        .multilineTextAlignment(.leading)
                        .padding(.horizontal, 16)
                        .padding(.bottom, 10)
                    
                    Spacer()
                }
                
                MovieListView(movieList: contentMovieList.movies ?? [])
            }
            .frame(width: UIScreen.main.bounds.width)
            .padding(.vertical, 16)
        }
        .background(backgroundColor)
    }
}

struct ContentMovieListView_Previews: PreviewProvider {
    static var previews: some View {
        ContentMovieListView(contentMovieList: Content.exampleMovieList)
    }
}
