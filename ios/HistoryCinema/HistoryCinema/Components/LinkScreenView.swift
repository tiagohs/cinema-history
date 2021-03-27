//
//  LinkScreenView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/03/21.
//

import SwiftUI

struct LinkScreenView: View {
    var subtitle: String?
    var title: String?
    var description: String?
    var image: LocalImage?
    
    var body: some View {
        let color = ColorUtils.getRandomColorAssets()
        let color = Color(UIColor(colorName: "md_\(color.colorName)_500"))
        
        VStack {
            if subtitle != nil {
                Text(subtitle ?? "")
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .font(.oswaldLight(size: 12))
                    .foregroundColor(Color.white)
                    .padding(.horizontal, 16)
                    .padding(.top, 12)
            }
            
            if title != nil {
                Text(title ?? "")
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .font(.oswaldBold(size: 16))
                    .foregroundColor(color)
                    .padding(.horizontal, 16)
                    .padding(.bottom, 5)
            }
            
            if description != nil {
                Text(description ?? "")
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .font(.proximaNovaRegular(size: 12))
                    .foregroundColor(Color.white)
                    .padding(.horizontal, 16)
                    .padding(.bottom, 5)
            }
            
            HStack {
                Spacer()
                
                Button(action: {
                    print("Ir Para Pages")
                }) {
                    Text("Iniciar a Jornada")
                        .font(.proximaNovaRegular(size: 18))
                        .padding(.horizontal, 22)
                        .padding(.vertical, 12)
                        .background(color)
                        .foregroundColor(Color.white)
                        .cornerRadius(5)
                }
                .padding(.trailing, 16)
                .padding(.bottom, 12)
            }
        }
        .background(ZStack {
            CustomImage(image: image, type: .movie)
                .frame(width: UIScreen.main.bounds.width)
            
            Rectangle()
                .fill(Color(UIColor(colorName: "md_black_1000")))
                .opacity(0.6)
                .frame(width: UIScreen.main.bounds.width)
        })
        .frame(
            width: UIScreen.main.bounds.width - 32)
        .cornerRadius(25)
    }
}

struct LinkScreenView_Previews: PreviewProvider {
    static var previews: some View {
        LinkScreenView(
            subtitle: "Os acontecimentos que marcaram o mundo",
            title: "Timeline de Acontecimentos de 1970 a 1989",
            description: "O exorcista é lançado, fazendo plateias no mundo todo passarem mal e desmaiarem, criando uma histeria global. O primeiro blockbuster é lançado: Tubarão, em paralelo, o cinema autoral ganha cresce com Martin Scorsese e outros grandes diretores. Os anos 80 vem acompanhado com produções de baixo orçamento e suas comédias que marcaram época. Clique aqui e veja de forma cronológica os principais acontecimentos do cinema entre 1940 a 1959."
        )
    }
}
