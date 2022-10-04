//
//  MovieContentSummaryView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 30/09/22.
//

import SwiftUI

struct MovieContentSummaryView: View {
    let movie: Movie!
    
    var body: some View {
        MovieContentContainer {
            VStack(alignment: .leading) {
                HStack {
                    if let ranting = movie.ranting() {
                        Text(ranting.rating)
                            .font(.proximaNovaRegular(size: 18))
                            .multilineTextAlignment(.leading)
                            .padding(8)
                            .foregroundColor(ranting.textColor)
                            .background(ranting.backgroundColor)
                            .cornerRadius(5)
                    }
                    
                    if let externalLinks = movie.externalLinks() {
                        ForEach(externalLinks, id: \.self) { externalLink in
                            Button(action: {
                                if let url = URL(string: externalLink.link) {
                                   UIApplication.shared.open(url)
                                }
                            }) {
                                HStack {
                                    Image(externalLink.image)
                                        .resizable()
                                        .frame(width: 25, height: 25)
                                }
                                .foregroundColor(.cardButtonTextColor)
                                .background(Color.cardButtonBackgroundColor)
                                .cornerRadius(25)
                                .padding(5)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 5)
                                        .stroke(Color(.black), lineWidth: 1)
                                )
                            }
                        }
                        
                    }
                }
                
                if let summary = movie.summary() {
                    Text("Sinopse")
                        .font(.oswaldRegular(size: 24))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 5)
                    
                    Text(summary)
                        .font(.proximaNovaRegular(size: 16))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textPrimary)
                        .padding(.bottom, 5)
                    
                    Text("Fonte: TMDB")
                        .font(.oswaldRegular(size: 12))
                        .multilineTextAlignment(.leading)
                        .foregroundColor(.textSecondary)
                }
            }
            .padding(20)
        }
    }
}

struct MovieContentSummaryView_Previews: PreviewProvider {
    static var previews: some View {
        MovieContentSummaryView(movie: Movie.exampleMovieFull())
    }
}
