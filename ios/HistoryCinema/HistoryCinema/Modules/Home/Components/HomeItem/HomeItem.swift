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
        VStack {
            Text("A História do")
                .font(.oswaldBold(size: 30))
                .multilineTextAlignment(.center)
                .foregroundColor(.white)
                
            Text("Cinema")
                .font(.billionaireMediumGrunge(size: 150))
                .multilineTextAlignment(.center)
                .foregroundColor(.white)
        }
    }
}

struct OneThousandsMoviesTitle: View {
    var body: some View {
        VStack {
            Text("1001 Filmes")
                .font(.bigshouldersDisplayBold(size: 50))
                .multilineTextAlignment(.center)
                .foregroundColor(.white)
                
            Text("para Ver antes de Morrer")
                .font(.oswaldBold(size: 24))
                .multilineTextAlignment(.center)
                .textCase(.uppercase)
                .foregroundColor(.white)
        }
    }
}

struct AwardsTitle: View {
    var body: some View {
        VStack {
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
        }
    }
}

struct TimelineTitleView: View {
    var body: some View {
        VStack {
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
        }
    }
}

struct DirectorsTitle: View {
    var body: some View {
        VStack {
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
        }
    }
}

struct HomeItemTitle: View {
    let homeContentItem: HomeContentItem
    
    var body: some View {
        VStack(alignment: .center) {
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
        let cardHeight = UIScreen.main.bounds.height - (UIScreen.main.bounds.height / 3)
        
        ZStack(alignment: Alignment(horizontal: .center, vertical: .top)) {
            ZStack(alignment: .center) {
                HomeItemImage(
                    homeContentItem: homeContentItem,
                    height: cardHeight
                )
                
                HomeDegrade()
            }
            .frame(
                width: UIScreen.main.bounds.width - 32,
                height: CGFloat(cardHeight))
            
            HomeItemTitle(homeContentItem: homeContentItem)
                .padding(.top, 30)
        }
        .frame(
            width: UIScreen.main.bounds.width - 32,
            height: CGFloat(cardHeight)
        )
        .cornerRadius(25)
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
