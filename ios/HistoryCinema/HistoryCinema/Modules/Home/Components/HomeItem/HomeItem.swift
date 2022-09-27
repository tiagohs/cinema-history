//
//  HomeItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/02/21.
//

import SwiftUI

struct HomeItemImage: View {
    let homeContentItem: HomeContentItem
    let height: CGFloat
    
    var body: some View {
        CustomImage(
            image: homeContentItem.image,
            imageType: .local,
            placeholderType: .movie,
            height: Int(height)
        )
    }
}

struct HistoryCinemaTitle: View {
    var body: some View {
        VStack(alignment: .leading) {
            Text("A História do")
                .font(.oswaldBold(size: 20))
                .foregroundColor(Color.cardTextPrimary)
                
            Text("Cinema")
                .font(.billionaireMediumGrunge(size: 40))
                .foregroundColor(Color.cardTextPrimary)
            
            Text("Entenda em detalhes os momentos cruciais na história da sétima arte!")
                .font(.proximaNovaRegular(size: 15))
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.cardTextPrimary)
                .padding(.top, 3)
        }
    }
}

struct OneThousandsMoviesTitle: View {
    var body: some View {
        VStack(alignment: .leading) {
            Text("1001 Filmes")
                .font(.bigshouldersDisplayBold(size: 30))
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.cardTextPrimary)
                
            Text("para Ver antes de Morrer")
                .font(.oswaldBold(size: 18))
                .textCase(.uppercase)
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.cardTextPrimary)
            
            Text("Uma seleção especial dos melhores filmes lançados desde os primórdios do cinema.")
                .font(.proximaNovaRegular(size: 15))
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.cardTextPrimary)
                .padding(.top, 3)
        }
    }
}

struct AwardsTitle: View {
    var body: some View {
        VStack(alignment: .leading) {
            Text("Premiações do Cinema")
                .font(.futuraBold(size: 18))
                .textCase(.uppercase)
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.cardTextPrimary)
                
            Text("Conheça os prêmios mais importantes do cinema")
                .font(.proximaNovaRegular(size: 15))
                .foregroundColor(Color.cardTextPrimary)
                .multilineTextAlignment(.leading)
                .padding(.top, 3)
        }
    }
}

struct TimelineTitleView: View {
    var body: some View {
        VStack(alignment: .leading) {
            Text("Timeline do Cinema")
                .font(.oswaldBold(size: 20))
                .textCase(.uppercase)
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.cardTextPrimary)
                
            Text("Os principais acontecimentos no mundo do cinema organizada cronologicamente.")
                .font(.proximaNovaRegular(size: 15))
                .foregroundColor(Color.cardTextPrimary)
                .multilineTextAlignment(.leading)
                .padding(.top, 3)
        }
    }
}

struct DirectorsTitle: View {
    var body: some View {
        VStack(alignment: .leading) {
            Text("Os Mestres nas telas")
                .font(.oswaldBold(size: 20))
                .textCase(.uppercase)
                .multilineTextAlignment(.leading)
                .foregroundColor(Color.cardTextPrimary)
                
            Text("Conheça em detalhes os maiores diretores do Cinema")
                .font(.proximaNovaRegular(size: 15))
                .foregroundColor(Color.cardTextPrimary)
                .multilineTextAlignment(.leading)
                .padding(.top, 3)
        }
    }
}

struct HomeItemTitle: View {
    let homeContentItem: HomeContentItem
    
    var body: some View {
        VStack(alignment: .leading) {
            switch homeContentItem.mainTopicType {
                case .history_cinema: HistoryCinemaTitle()
                case .mil_movies: OneThousandsMoviesTitle()
                case .awards: AwardsTitle()
                case .timeline: TimelineTitleView()
                case .directors: DirectorsTitle()
                default: EmptyView()
            }
        }
    }
}

struct HomeDegrade: View {
    var body: some View {
        let height = CGFloat(200)
        
        VStack {
            LinearGradient(
                gradient: Gradient(colors: [.black, .black, .clear]),
                startPoint: .top,
                endPoint: .bottom
            )
            .frame(height: height)

            Spacer()

            LinearGradient(
                gradient: Gradient(colors: [.black, .black, .clear]),
                startPoint: .bottom,
                endPoint: .top
            )
            .frame(height: height)
        }
    }
}

struct HomeItem: View {
    
    let homeContentItem: HomeContentItem
    
    var body: some View {
        VStack(alignment: .leading) {
            Image(homeContentItem.image!.url!)
                .resizable()
                .aspectRatio(contentMode: .fill)
                .frame(height: 250)
            
            HStack {
                HomeItemTitle(homeContentItem: homeContentItem)
                
                Spacer()
                
                Button(action: {
                    
                }) {
                    HStack {
                        Text("Iniciar jornada")
                            .font(.proximaNovaBold(size: 14))
                    }
                    .padding()
                    .foregroundColor(.cardButtonTextColor)
                    .background(Color.cardButtonBackgroundColor)
                    .cornerRadius(40)
                }
            }
            .padding()
            .background(Color.cardBackground)
        }
        .cornerRadius(25)
        .shadow(radius: 5)
        .overlay(
            RoundedRectangle(cornerRadius: 25)
                .stroke(Color(.sRGB, red: 150/255, green: 150/255, blue: 150/255, opacity: 0.1), lineWidth: 1)
        )
        .padding()
    }
}

struct HomeItem_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            HomeItem(homeContentItem: HomeContentItem.example(MainTopicsType.mil_movies))
            HomeItem(homeContentItem: HomeContentItem.example(MainTopicsType.history_cinema))
            HomeItem(homeContentItem: HomeContentItem.example(MainTopicsType.awards))
            HomeItem(homeContentItem: HomeContentItem.example(MainTopicsType.directors))
            HomeItem(homeContentItem: HomeContentItem.example(MainTopicsType.timeline))
        }
    }
}
