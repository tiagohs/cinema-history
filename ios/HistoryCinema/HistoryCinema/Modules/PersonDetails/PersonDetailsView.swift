//
//  PersonDetailsView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 04/10/22.
//

import SwiftUI

struct PersonDetailsView: View {
    
    let personId: Int!
    
    @ObservedObject private var presenter: PersonDetailsPresenter = PersonDetailsWireframe.buildPresenter()
    
    @Environment(\.presentationMode) var presentationMode: Binding<PresentationMode>
    
    var body: some View {
        ZStack(alignment: .topLeading) {
            ScrollView {
                VStack {
                    if presenter.isLoading {
                        ProgressView()
                            .padding()
                    }
                    
                    if !presenter.isLoading {
                        PersonDetailsContentView(person: presenter.person)
                    }
                }
            }
            .frame(maxWidth: .infinity)
            .edgesIgnoringSafeArea(.all)
            
            Button(action: {
                self.presentationMode.wrappedValue.dismiss()
            }) {
                Image(systemName: "xmark")
                    .font(.system(size: 16, weight: .bold))
                    .padding()
                    .background(Color.backgroundInverse)
                    .foregroundColor(Color.textInverse)
                    .clipShape(Circle())
            }
            .shadow(color: Color.backgroundInverse, radius: 5)
            .padding()
        }
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema! Por favor, tente novamente."),
                  dismissButton: .default(Text("Tentar novamente")) {
                    presenter.fetchPersonDetailsBy(personId)
                  }
            )
        })
        .onAppear {
            presenter.viewAppears()
            presenter.fetchPersonDetailsBy(personId)
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct PersonDetailsView_Previews: PreviewProvider {
    static var previews: some View {
        PersonDetailsView(personId: 3223)
    }
}
