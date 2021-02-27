//
//  HomeItem.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 20/02/21.
//

import SwiftUI

struct HomeItemImage: View {
    let homeContentItem: HomeContentItem
    
    var body: some View {
        if homeContentItem.mainTopicType == .mil_movies {
            Image(homeContentItem.image!.url!)
                .resizable()
                .frame(
                    width: UIScreen.main.bounds.width,
                    height: UIScreen.main.bounds.height
                )
        } else {
            Image(homeContentItem.image!.url!)
                .resizable()
                .scaledToFill()
                .frame(
                    height: 500
                )
        }
    }
}

struct HomeItemTitle: View {
    let homeContentItem: HomeContentItem
    
    var body: some View {
        VStack(alignment: .center) {
            
            switch homeContentItem.mainTopicType {
            case .history_cinema:
                Text("A História do")
                    .font(.oswaldBold(size: 30))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
                    
                Text("Cinema")
                    .font(.billionaireMediumGrunge(size: 150))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
            
            case .mil_movies:
                Text("1001 Filmes")
                    .font(.bigshouldersDisplayBold(size: 50))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
                    
                Text("para Ver antes de Morrer")
                    .font(.oswaldBold(size: 24))
                    .multilineTextAlignment(.center)
                    .textCase(.uppercase)
                    .foregroundColor(.white)
            case .awards:
                Text("Premiações do Cinema")
                    .font(.futuraCondensedLight(size: 32))
                    .multilineTextAlignment(.center)
                    .textCase(.uppercase)
                    .foregroundColor(.white)
                    
                Text("Conheça os prêmios mais importantes do cinema")
                    .font(.proximaNovaRegular(size: 18))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
                    .padding(.horizontal, 12)
            case .timeline:
                Text("Timeline do Cinema")
                    .font(.oswaldBold(size: 32))
                    .multilineTextAlignment(.center)
                    .textCase(.uppercase)
                    .foregroundColor(.white)
                    
                Text("Os principais acontecimentos no mundo do cinema organizada cronologicamente.")
                    .font(.proximaNovaRegular(size: 18))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
                    .padding(.horizontal, 12)
            case .directors:
                Text("Os Mestres nas telas")
                    .font(.oswaldBold(size: 32))
                    .multilineTextAlignment(.center)
                    .textCase(.uppercase)
                    .foregroundColor(.white)
                    
                Text("Conheça em Detalhes os maiores diretores do Cinema")
                    .font(.proximaNovaRegular(size: 18))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
                    .padding(.horizontal, 12)
            default:
                Text("")
            }
        }
    }
}

struct HomeItem: View {
    
    let homeContentItem: HomeContentItem
    
    var body: some View {
        VStack {
            ZStack(alignment: Alignment(horizontal: .center, vertical: .top)) {
                ZStack(alignment: .center) {
                    VStack {
                        HomeItemImage(homeContentItem: homeContentItem)
                    }
                    
                    VStack {
                        LinearGradient(
                            gradient: Gradient(colors: [.black, .black, .clear]),
                            startPoint: .top,
                            endPoint: .bottom
                        )
                        .frame(height: 200)

                        Spacer()

                        LinearGradient(
                            gradient: Gradient(colors: [.black, .black, .clear]),
                            startPoint: .bottom,
                            endPoint: .top
                        )
                        .frame(height: 200)
                    }
                }
                .frame(
                    width: UIScreen.main.bounds.width - 32,
                    height: 600)
                
                HomeItemTitle(homeContentItem: homeContentItem)
                    .padding(.top, 30)
            }
            .frame(
                width: UIScreen.main.bounds.width - 32,
                height: 600)
            .cornerRadius(25)
        }
    }
}

struct HomeItem_Previews: PreviewProvider {
    static var previews: some View {
        Group {
            HomeItem(homeContentItem: HomeContentItem.example(MainTopicsType.mil_movies))
            HomeItem(homeContentItem: HomeContentItem.example(MainTopicsType.history_cinema))
        }
    }
}
