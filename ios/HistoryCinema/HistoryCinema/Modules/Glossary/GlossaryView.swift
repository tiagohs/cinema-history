//
//  GlossaryView.swift
//  HistoryCinema
//
//  Created by Tiago Henrique da Silva on 10/10/22.
//

import SwiftUI

struct GlossaryView: View {
    
    @ObservedObject private var presenter: GlossaryPresenter = GlossaryWireframe.buildPresenter()
    
    var body: some View {
        VStack {
            if (presenter.glossaryList.count == 0) {
                ProgressView()
            }
            
            if !presenter.glossaryList.isEmpty {
                GlossaryContentView(glossaryList: presenter.glossaryList)
            }
        }
        .navigationTitle("Glossário")
        .navigationBarTitleDisplayMode(.inline)
        .foregroundColor(Color.textPrimary)
        .alert(isPresented: $presenter.showErrorMessage, content: {
            Alert(title: Text("Ops"),
                  message: Text("Houve algum problema! Por favor, tente novamente."),
                  dismissButton: .default(Text("Tentar novamente")) { presenter.fetchGlossary()
                
            }
            )
        })
        .onAppear {
            presenter.viewAppears()
            presenter.fetchGlossary()
        }
        .onDisappear { presenter.viewDisappears() }
    }
        
}

struct GlossaryView_Previews: PreviewProvider {
    static var previews: some View {
        GlossaryView()
    }
}
