//
//  MovieContentWatchOn.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 03/10/22.
//

import SwiftUI

struct MovieContentWatchOn: View {
    let movieExtraInfo: MovieExtraInfo?
    
    var body: some View {
        if let watchOnList = self.movieExtraInfo?.watchOn, !watchOnList.isEmpty {
            MovieContentContainer {
                VStack(alignment: .leading) {
                    Text("Onde assistir")
                        .font(.oswaldRegular(size: 24))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 5)
                    
                    HStack {
                        ForEach(0 ..< watchOnList.count) { index in
                            let watchOn = watchOnList[index]
                            let networkInfo = NetworkType.getNetwork(watchOn.type)
                            let textColor = Color(UIColor(colorName: networkInfo.textColor))
                            let backgroundColor = Color(UIColor(colorName: networkInfo.color))
                            
                            Button(action: {
                                if let url = URL(string: watchOn.link ?? "") {
                                   UIApplication.shared.open(url)
                                }
                            }) {
                                Text(networkInfo.networkName ?? "")
                                    .font(.proximaNovaBold(size: 18))
                                    .multilineTextAlignment(.center)
                                    .foregroundColor(textColor)
                                    .padding(10)
                                    .background(backgroundColor)
                                    .cornerRadius(12)
                            }
                        }
                    }
                }
                .padding(20)
            }
        } else {
            AnyView(EmptyView())
        }
    }
}

struct MovieContentWatchOn_Previews: PreviewProvider {
    static var previews: some View {
        MovieContentWatchOn(movieExtraInfo: Movie.exampleMovieExtraInfo)
    }
}
