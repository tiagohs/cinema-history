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
        if homeContentItem.mainTopicType == .milMovies {
            Image(homeContentItem.image!.url!)
                .resizable()
                .frame(
                    width: UIScreen.main.bounds.width,
                    height: UIScreen.main.bounds.height
                )
        } else {
            Image(homeContentItem.image!.url!)
                .resizable()
                .frame(
                    width: UIScreen.main.bounds.width,
                    height: UIScreen.main.bounds.height - 200
                )
        }
        
    }
}

struct HomeItemTitle: View {
    let homeContentItem: HomeContentItem
    
    var body: some View {
        VStack(alignment: .center) {
            
            switch homeContentItem.mainTopicType {
            case .historyCinema:
                Text("A História do")
                    .font(.oswaldBold(size: 30))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
                    
                Text("Cinema")
                    .font(.billionaireMediumGrunge(size: 150))
                    .multilineTextAlignment(.center)
                    .foregroundColor(.white)
            
            case .milMovies:
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
                .ignoresSafeArea()
                .background(Color.black)
                
                HomeItemTitle(homeContentItem: homeContentItem)
                    .padding(.top, 30)
            }
        }
    }
}

struct HomeItem_Previews: PreviewProvider {
    static var previews: some View {
        let homeItemList: [HomeContentItem] = load("homecontent.json")
        
        Group {
            HomeItem(homeContentItem: homeItemList[0])
            HomeItem(homeContentItem: homeItemList[1])
            HomeItem(homeContentItem: homeItemList[2])
            HomeItem(homeContentItem: homeItemList[3])
            HomeItem(homeContentItem: homeItemList[4])
        }
        
    }
}
