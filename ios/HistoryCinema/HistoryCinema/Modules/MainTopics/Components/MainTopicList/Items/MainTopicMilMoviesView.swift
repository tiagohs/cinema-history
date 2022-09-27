//
//  MainTopicMilMovies.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/02/21.
//

import SwiftUI

struct MainTopicMilMoviesView: View {
    let milMoviesMainTopic: MilMoviesMainTopic
    
    var body: some View {
        let image = milMoviesMainTopic.image
        let imageHeight = milMoviesMainTopic.image.imageStyle?.height ?? 300
        
        ZStack(alignment: Alignment(horizontal: .center, vertical: .bottom)) {
            Image(image!.url!)
                .resizable()
                .frame(height: CGFloat(imageHeight))
            
            LinearGradient(
                gradient: Gradient(colors: [.black, .clear, .clear]),
                startPoint: .bottom,
                endPoint: .top
            )
            .cornerRadius(25)
            .frame(
                height: CGFloat(300)
            )
            
            HStack {
                Text(milMoviesMainTopic.title!)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .font(.oswaldBold(size: 28))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(Color.white)
                
                Spacer()
                
                Image(systemName: "chevron.right")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundColor(Color.white)
            }
            .padding()
        }
        .cornerRadius(25)
        .overlay(
            RoundedRectangle(cornerRadius: 25)
                .stroke(Color(.sRGB, red: 150/255, green: 150/255, blue: 150/255, opacity: 0.1), lineWidth: 1)
        )
        .padding()
    }
}

struct MainTopicMilMovies_Previews: PreviewProvider {
    static var previews: some View {
        let mainTopic = MainTopic.example(MainTopicsType.mil_movies)
        
        MainTopicMilMoviesView(milMoviesMainTopic: mainTopic as! MilMoviesMainTopic)
    }
}
