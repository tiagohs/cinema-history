//
//  Placeholder.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 15/03/21.
//

import SwiftUI

enum PlaceholderType {
    case movie
    case person
    case video
}

struct Placeholder: View {
    let type: PlaceholderType
    var iconSize: CGFloat?
    
    var body: some View {
        let size = iconSize ?? 200
        
        ZStack {
            Color.black
                .ignoresSafeArea()
            switch type {
            case .movie:
                Icon(name: .movie)
                    .fill(Color.white)
                    .frame(width: size, height: size)
            case .person:
                Icon(name: .person)
                    .fill(Color.white)
                    .frame(width: size, height: size)
            case .video:
                Icon(name: .video)
                    .fill(Color.white)
                    .frame(width: size, height: size)
            }
        }
    }
}

struct Placeholder_Previews: PreviewProvider {
    static var previews: some View {
        Placeholder(type: .person)
    }
}
