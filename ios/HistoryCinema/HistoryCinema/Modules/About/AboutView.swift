//
//  AboutView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 27/10/22.
//

import SwiftUI

struct AboutView: View {
    
    @ObservedObject private var presenter: AboutPresenter = AboutWireframe.buildPresenter()
    
    var body: some View {
        let appVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""

        NavigationView {
            List {
                VStack(alignment: .center) {
                    Image(uiImage: UIImage(named: "AppIcon") ?? UIImage())
                        .resizable()
                        .frame(width: 100, height: 100)
                        .cornerRadius(5)
                    
                    Text("'História do Cinema' é um aplicativo grátis que busca, de uma forma divertida e intuitiva, levar a história da sétima arte a todos. Através de imagens, vídeos, entrevistas e infográficos, apresentaremos a trajetória do cinema desde a primeira exibição dos irmãos Lumière até os dias de hoje. Nos baseamos na literatura e em documentários e traduzimos para essa nova mídia. Divirta-se!")
                        .font(.proximaNovaRegular(size: 16))
                        .multilineTextAlignment(.center)
                        .foregroundColor(Color.textPrimary)
                        .padding(.vertical)
                }
                .frame(maxWidth: .infinity)
                
                // Desenvolvimento
                Section("Desenvolvimento") {
                    Text("Versão \(appVersion)")
                        .multilineTextAlignment(.leading)
                        .foregroundColor(Color.textPrimary)
                    Text("Desenvolvimento e Pesquisa por Tiago Silva")
                        .multilineTextAlignment(.leading)
                        .foregroundColor(Color.textPrimary)
                    AboutLinkItemView(
                        link: "mailto:tiago.silva.93@hotmail.com",
                        text: "Entre em contato"
                    )
                }
                
                // Termos e Créditos
                Section("Termos e Créditos") {
                    AboutLinkItemView(
                        link: "https://animated-stickers.s3-sa-east-1.amazonaws.com/history_terms.html",
                        text: "Termos do Aplicativo"
                    )
                    AboutLinkItemView(
                        link: "https://www.themoviedb.org/terms-of-use",
                        text: "TMDB Termos de Uso"
                    )
                    AboutLinkItemView(
                        link: "https://www.themoviedb.org/documentation/api/terms-of-use",
                        text: "TMDB API Termos de Uso"
                    )
                    AboutLinkItemView(
                        link: "http://www.omdbapi.com/legal.htm",
                        text: "OMDB API Termos de Uso"
                    )
                    AboutLinkItemView(
                        link: "https://animated-stickers.s3-sa-east-1.amazonaws.com/history_privacy_politics.html",
                        text: "Política de Privacidade"
                    )
                    AboutLinkItemView(
                        link: "https://creativecommons.org/licenses/by/3.0/br/",
                        text: "Creative Commons — Atribuição 3.0 Brasil — CC BY 3.0 BR"
                    )
                    
                    Text("As informações dos filmes, atores, diretores e as imagens são fornecidas pela API TMDb mas não é endossado ou certificado pelo TMDb.")
                        .multilineTextAlignment(.leading)
                        .foregroundColor(Color.textPrimary)
                }
                
                
                VStack(alignment: .center) {
                    Text("Copyrights © 2022")
                        .font(.proximaNovaRegular(size: 14))
                        .multilineTextAlignment(.center)
                        .foregroundColor(Color.textPrimary)
                }
                .frame(maxWidth: .infinity)
            }
            .listStyle(.grouped)
        }
        .navigationBarTitleDisplayMode(.inline)
        .foregroundColor(Color.textPrimary)
        .toolbar {
            ToolbarItem(placement: .principal) {
                HStack(alignment: .center) {
                    Text("Sobre")
                        .font(.oswaldBold(size: 18))
                        .foregroundColor(Color.textPrimary)
                }
            }
        }
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema!"),
                  dismissButton: .default(Text("Ok")) {
                    
                  }
            )
        })
        .onAppear {
            presenter.viewAppears()
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct AboutView_Previews: PreviewProvider {
    static var previews: some View {
        AboutView()
    }
}
