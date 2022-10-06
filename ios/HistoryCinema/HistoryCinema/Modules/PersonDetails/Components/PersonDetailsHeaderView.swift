//
//  PersonDetailsHeaderView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 05/10/22.
//

import SwiftUI

struct PersonHeaderLeftView: View {
    let person: Person!
    
    var body: some View {
        VStack(alignment: .leading) {
            if let name = person.name {
                Text(name)
                    .font(.oswaldBold(size: 30))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.white)
            }
            
            if let birthdayFull = person.birthdayFull() {
                Text(birthdayFull)
                    .font(.proximaNovaRegular(size: 22))
                    .multilineTextAlignment(.leading)
                    .foregroundColor(.white)
            }
            
            if let departments = person.departments(), !departments.isEmpty {
                HStack {
                    ForEach(Array(departments.enumerated()), id: \.offset) { index, department in
                        if (index <= 2) {
                            Text(department)
                                .font(.oswaldBold(size: 14))
                                .multilineTextAlignment(.center)
                                .foregroundColor(.white)
                                .padding(.horizontal, 16)
                                .padding(.vertical, 8)
                                .background(.black)
                                .cornerRadius(20)
                        }
                    }
                }
            }
        }
        .padding()
        .layoutPriority(100)
    }
}


struct PersonHeaderRightView: View {
    let person: Person!
    
    var body: some View {
        VStack {
            if let externalLinks = person.externalLinks() {
                ForEach(externalLinks, id: \.self) { externalLink in
                    Button(action: {
                        if let url = URL(string: externalLink.link) {
                           UIApplication.shared.open(url)
                        }
                    }) {
                        HStack {
                            Image(externalLink.image)
                                .resizable()
                                .renderingMode(.template)
                                .foregroundColor(Color.white)
                                .frame(width: 25, height: 25)
                        }
                        .cornerRadius(25)
                        .padding(10)
                        .overlay(
                            Circle()
                                .stroke(Color(.white), lineWidth: 1)
                        )
                    }
                }
            }
        }
        .padding(.vertical)
        .padding(.horizontal, 20)
    }
}


struct PersonDetailsHeaderView: View {
    let person: Person!
    
    var body: some View {
        let profileImage = ImageUtils.formatImageUrl(imageID: person.profilePath, imageSize: TMDB.ImageSize.PROFILE.h632) ?? ""
        let imageHeight = 500
        
        ZStack(alignment: Alignment(horizontal: .leading, vertical: .bottom)) {
            CustomImage(
                imageUrl: profileImage,
                imageType: .online,
                placeholderType: .person,
                height: imageHeight
            )
                .clipped()
                .frame(height: CGFloat(imageHeight))
            
            Rectangle()
                .background(.black)
                .opacity(0.1)
            
            HStack(alignment: .bottom) {
                PersonHeaderLeftView(person: person)
                
                Spacer()
                
                PersonHeaderRightView(person: person)
            }
            .padding(.bottom, 10)
        }
        .frame(height: CGFloat(imageHeight))
    }
}

struct PersonDetailsHeaderView_Previews: PreviewProvider {
    static var previews: some View {
        PersonDetailsHeaderView(person: Person.examplePersonFull)
    }
}
