//
//  MovieContentInfoView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 30/09/22.
//

import SwiftUI

struct MovieContentInfoView: View {
    let movie: Movie!
    
    var body: some View {
        let posterImageUrl = ImageUtils.formatImageUrl(imageID: movie.posterPath, imageSize: TMDB.ImageSize.POSTER.w780) ?? ""
        
        MovieContentContainer {
            HStack(alignment: .top) {
                VStack(alignment: .leading) {
                    if let directors = movie.directors(), !directors.isEmpty {
                        Text("Dirigido por")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(directors)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let writers = movie.writers(), !writers.isEmpty  {
                        Text("Escrito por")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(writers)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let originalLanguage = movie.language(), !originalLanguage.isEmpty {
                        Text("Idioma original")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(originalLanguage)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let runtime = movie.duration(), !runtime.isEmpty {
                        Text("Duração")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(runtime)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let releaseDate = movie.releaseDate?.toDate()?.formatDate(pattner: "dd/MM/yyyy"), !releaseDate.isEmpty {
                        Text("Lançamento")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(releaseDate)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    
                    if let budget = movie.budget, budget != 0, let budgetFormat = budget.toCurrency()  {
                        Text("Orçamento")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(budgetFormat)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                    if let revenue = movie.revenue, revenue != 0, let revenueFormat = revenue.toCurrency() {
                        Text("Receita")
                            .font(.proximaNovaRegular(size: 14))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textSecondary)
                        
                        Text(revenueFormat)
                            .font(.proximaNovaRegular(size: 16))
                            .multilineTextAlignment(.leading)
                            .foregroundColor(.textPrimary)
                            .padding(.bottom, 5)
                    }
                    
                }
                .padding(.leading, 20)
                .padding(.vertical, 25)
                
                Spacer()
                
                VStack {
                    CustomImage(
                        imageUrl: posterImageUrl,
                        imageType: .online,
                        placeholderType: .movie,
                        width: 150,
                        height: 200,
                        cornerRadius: 20
                    )
                    .frame(width: 150, height: 200)
                }
                .padding(.vertical, 35)
                .padding(.trailing, 20)
            }
        }
    }
}

struct MovieContentInfoView_Previews: PreviewProvider {
    static var previews: some View {
        MovieContentInfoView(movie: Movie.exampleMovieFull()) 
    }
}
