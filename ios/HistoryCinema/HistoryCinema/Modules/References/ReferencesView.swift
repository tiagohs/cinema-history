//
//  ReferencesView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 06/10/22.
//

import SwiftUI

struct ReferencesView: View {
    
    @ObservedObject private var presenter: ReferencesPresenter = ReferencesWireframe.buildPresenter()
    
    var body: some View {
        VStack {
            if (presenter.references.count == 0) {
                ProgressView()
            }
            
            if !presenter.references.isEmpty {
                ReferenceContentListView(references: presenter.references)
            }
        }
        .navigationTitle("Referências e Sugestões")
        .navigationBarTitleDisplayMode(.inline)
        .foregroundColor(Color.textPrimary)
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema! Por favor, tente novamente."),
                  dismissButton: .default(Text("Tentar novamente")) { presenter.fetchReferences()
                
            }
            )
        })
        .onAppear {
            presenter.viewAppears()
            presenter.fetchReferences()
        }
        .onDisappear { presenter.viewDisappears() }
    }
}

struct ReferencesView_Previews: PreviewProvider {
    static var previews: some View {
        ReferencesView()
    }
}
